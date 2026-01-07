document.addEventListener("DOMContentLoaded", () => {

    /**
     * Update all .count spans from a new HTML document
     */
    function updateInventoryCounts(newDoc) {
        const newCounts = newDoc.querySelectorAll(".actions__icons .count");
        newCounts.forEach(newSpan => {
            const labelText = newSpan.closest(".icon-label").textContent.trim().split(" ")[0];
            const liveSpan = Array.from(document.querySelectorAll(".actions__icons .icon-label"))
                                  .find(el => el.textContent.trim().startsWith(labelText))
                                  ?.querySelector(".count");
            if (liveSpan) liveSpan.textContent = newSpan.textContent;
        });
    }

    /**
     * Bind all inventory forms to AJAX submission
     */
    function bindInventoryForms() {
        const inventoryForms = document.querySelectorAll(
            ".actions__icons form[action$='increase']"
        );

        inventoryForms.forEach(form => {
            if (form.dataset.inventoryBound === "true") return;
            form.dataset.inventoryBound = "true";

            form.addEventListener("submit", async (e) => {
                e.preventDefault();

                try {
                    const formData = new FormData(form);
                    const res = await fetch(form.action, {
                        method: (form.method || "POST").toUpperCase(),
                        body: formData,
                        headers: { "X-Requested-With": "fetch" }
                    });

                    if (!res.ok) throw new Error("POST failed");

                    // Get updated HTML and sync counts
                    const html = await res.text();
                    const parser = new DOMParser();
                    const doc = parser.parseFromString(html, "text/html");
                    updateInventoryCounts(doc);

                } catch (err) {
                    console.error("Inventory update failed, reloading:", err);
                    window.location.reload();
                }
            });
        });
    }

    /**
     * Hook into existing polling system
     * Ensures counts always sync after HUD/DOM replacements
     */
    if (window.extractAndPatch) {
        const originalExtractAndPatch = window.extractAndPatch;
        window.extractAndPatch = function(htmlText) {
            const parser = new DOMParser();
            const doc = parser.parseFromString(htmlText, "text/html");

            originalExtractAndPatch(htmlText);

            // Sync inventory counts after DOM update
            updateInventoryCounts(doc);

            // Rebind forms after replacement
            bindInventoryForms();
        };
    }

    // Initial binding
    bindInventoryForms();
});
