'use strict';

/**
 * Returns `text` with the five HTML-special characters replaced by their
 * entities: & < > " '
 */
function escapeHtml(text) {
  // TODO (Q8.3)
  throw new Error('escapeHtml is not implemented');
}

/**
 * Turns an API status code into text for people:
 *   'IN_TRANSIT' -> 'In transit', 'DELIVERED' -> 'Delivered'
 */
function formatStatus(status) {
  // TODO (Q8.3)
  throw new Error('formatStatus is not implemented');
}

/**
 * Builds the HTML for a parcel's tracking events. See the README for the exact
 * markup required.
 */
function renderEvents(events) {
  // TODO (Q8.3)
  throw new Error('renderEvents is not implemented');
}

/**
 * Asks the API for a parcel's tracking events and reports what happened.
 * `fetchFn` is injectable so the function can be tested without a network.
 * See the README for the required return values.
 */
async function fetchTracking(trackingNo, fetchFn = fetch) {
  // TODO (Q8.3)
  throw new Error('fetchTracking is not implemented');
}

// ---- Page wiring (provided - do not change) -------------------------------

if (typeof document !== 'undefined') {
  document.addEventListener('DOMContentLoaded', () => {
    const form = document.getElementById('track-form');
    const input = document.getElementById('tracking-no');
    const results = document.getElementById('results');

    form.addEventListener('submit', async (event) => {
      event.preventDefault();
      results.innerHTML = '<p>Loading...</p>';
      const outcome = await fetchTracking(input.value.trim());
      results.innerHTML = outcome.ok
        ? renderEvents(outcome.events)
        : `<p class="error">${escapeHtml(outcome.error)}</p>`;
    });
  });
}

if (typeof module !== 'undefined' && module.exports) {
  module.exports = { escapeHtml, formatStatus, renderEvents, fetchTracking };
}
