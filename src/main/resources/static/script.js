function setupPluginLifecycleHook() {
    document.body.addEventListener("htmx:afterSwap", (e) => {
        if (e.detail.target.id !== "main") return;

        if (window.currentPlugin) {
            const deinit = window[`deinitPlugin${window.currentPlugin}`];
            if (typeof deinit === "function") {
                deinit();
            }
        }

        const pluginRoot = document.querySelector("#main [data-plugin]");
        const pluginName = pluginRoot?.dataset.plugin;

        if (pluginName) {
            window.currentPlugin = pluginName;
            const init = window[`initPlugin${pluginName}`];
            if (typeof init === "function") {
                init();
            }
        } else {
            window.currentPlugin = null;
        }
    });
}

if (document.readyState === "loading") {
    document.addEventListener("DOMContentLoaded", setupPluginLifecycleHook);
} else {
    setupPluginLifecycleHook();
}
