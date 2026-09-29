'use strict';

const test = require('node:test');
const assert = require('node:assert/strict');
const fs = require('node:fs');
const path = require('node:path');

const { escapeHtml, formatStatus, renderEvents, fetchTracking } = require('../app.js');

const html = fs.readFileSync(path.join(__dirname, '..', 'index.html'), 'utf8');
const css = fs.readFileSync(path.join(__dirname, '..', 'styles.css'), 'utf8');

// ---------------------------------------------------------------- Q8.1 HTML

function tagContaining(source, tagName, needle) {
  const tags = source.match(new RegExp(`<${tagName}\\b[^>]*>`, 'gi')) || [];
  return tags.find((tag) => tag.includes(needle));
}

test('Q8.1 html declares its language', () => {
  assert.match(html, /<html[^>]*\blang="en"/i);
});

test('Q8.1 page uses header and main landmarks instead of generic divs', () => {
  assert.match(html, /<header\b/i);
  assert.match(html, /<main\b/i);
  assert.doesNotMatch(html, /<div class="header"/i);
  assert.doesNotMatch(html, /<div class="content"/i);
});

test('Q8.1 tracking number input is labelled and validated', () => {
  assert.match(html, /<label[^>]*\bfor="tracking-no"/i);
  const input = tagContaining(html, 'input', 'id="tracking-no"');
  assert.ok(input, 'expected an <input> with id="tracking-no"');
  assert.match(input, /\bname="trackingNo"/);
  assert.match(input, /\brequired\b/);
  assert.match(input, /\bpattern="SP\[0-9\]\{8\}"/);
});

test('Q8.1 form has a submit button', () => {
  assert.match(html, /<button[^>]*\btype="submit"/i);
});

test('Q8.1 results region is a section announced politely to screen readers', () => {
  const section = tagContaining(html, 'section', 'id="results"');
  assert.ok(section, 'expected <section id="results">');
  assert.match(section, /aria-live="polite"/);
});

// ----------------------------------------------------------------- Q8.2 CSS

test('Q8.2 event list uses flexbox that wraps', () => {
  const rule = css.match(/\.events\s*\{([^}]*)\}/g).join('\n');
  assert.match(rule, /display:\s*flex/);
  assert.match(rule, /flex-wrap:\s*wrap/);
});

test('Q8.2 media query stacks the cards on small screens', () => {
  assert.match(css, /@media\s*\(max-width:\s*\d+px\)\s*\{[\s\S]*flex-direction:\s*column/);
});

test('Q8.2 each status has its own colour', () => {
  const colours = ['collected', 'in_transit', 'delivered'].map((status) => {
    const match = css.match(new RegExp(`\\.status-${status}\\s*\\{([^}]*)\\}`));
    assert.ok(match, `expected a .status-${status} rule`);
    const colour = match[1].match(/(?:^|[;\s])(?:background(?:-color)?|color):\s*([^;]+)/);
    assert.ok(colour, `expected .status-${status} to set a colour`);
    return colour[1].trim().toLowerCase();
  });
  assert.equal(new Set(colours).size, 3, 'the three statuses must not share a colour');
});

// ------------------------------------------------------------------- Q8.3 JS

test('escapeHtml escapes all five special characters', () => {
  assert.equal(escapeHtml(`<a href="x">Tom & 'Jerry'</a>`),
    '&lt;a href=&quot;x&quot;&gt;Tom &amp; &#39;Jerry&#39;&lt;/a&gt;');
});

test('escapeHtml escapes ampersands first (no double escaping)', () => {
  assert.equal(escapeHtml('<'), '&lt;');
});

test('escapeHtml leaves ordinary text alone', () => {
  assert.equal(escapeHtml('Cape Town'), 'Cape Town');
});

test('formatStatus makes status codes readable', () => {
  assert.equal(formatStatus('IN_TRANSIT'), 'In transit');
  assert.equal(formatStatus('DELIVERED'), 'Delivered');
  assert.equal(formatStatus('OUT_FOR_DELIVERY'), 'Out for delivery');
});

test('renderEvents shows a friendly message when there are no events', () => {
  assert.equal(renderEvents([]), '<p class="empty">No tracking events yet.</p>');
  assert.equal(renderEvents(undefined), '<p class="empty">No tracking events yet.</p>');
});

test('renderEvents renders one list item per event', () => {
  const out = renderEvents([
    { status: 'COLLECTED', location: 'Johannesburg', recordedAt: '2026-09-01T08:00:00' },
    { status: 'IN_TRANSIT', location: 'Bloemfontein', recordedAt: '2026-09-02T10:00:00' },
  ]);
  assert.ok(out.startsWith('<ul class="events">'));
  assert.ok(out.endsWith('</ul>'));
  assert.equal((out.match(/<li\b/g) || []).length, 2);
  assert.match(out, /class="event status-collected"/);
  assert.match(out, /class="event status-in_transit"/);
  assert.match(out, /In transit/);
  assert.match(out, /<time datetime="2026-09-02T10:00:00">/);
});

test('renderEvents escapes untrusted text so it cannot inject markup', () => {
  const out = renderEvents([
    { status: 'COLLECTED', location: '<script>alert(1)</script>', recordedAt: '2026-09-01T08:00:00' },
  ]);
  assert.doesNotMatch(out, /<script>/);
  assert.match(out, /&lt;script&gt;alert\(1\)&lt;\/script&gt;/);
});

function fakeFetch(response, calls = []) {
  return async (url) => {
    calls.push(url);
    if (response instanceof Error) throw response;
    return response;
  };
}

test('fetchTracking returns the events on success', async () => {
  const events = [{ status: 'COLLECTED', location: 'Durban', recordedAt: '2026-09-01T08:00:00' }];
  const calls = [];
  const outcome = await fetchTracking('SP00000001',
    fakeFetch({ ok: true, status: 200, json: async () => events }, calls));
  assert.deepEqual(outcome, { ok: true, events });
  assert.deepEqual(calls, ['/api/parcels/SP00000001/events']);
});

test('fetchTracking encodes the tracking number in the URL', async () => {
  const calls = [];
  await fetchTracking('SP 1/2', fakeFetch({ ok: true, status: 200, json: async () => [] }, calls));
  assert.deepEqual(calls, ['/api/parcels/SP%201%2F2/events']);
});

test('fetchTracking reports an unknown parcel on 404', async () => {
  const outcome = await fetchTracking('SP99999999', fakeFetch({ ok: false, status: 404 }));
  assert.deepEqual(outcome, { ok: false, error: 'Parcel not found' });
});

test('fetchTracking reports other HTTP errors generically', async () => {
  const outcome = await fetchTracking('SP00000001', fakeFetch({ ok: false, status: 500 }));
  assert.deepEqual(outcome, { ok: false, error: 'Something went wrong. Please try again.' });
});

test('fetchTracking reports network failures instead of throwing', async () => {
  const outcome = await fetchTracking('SP00000001', fakeFetch(new TypeError('Failed to fetch')));
  assert.deepEqual(outcome, {
    ok: false,
    error: 'Network error. Check your connection and try again.',
  });
});
