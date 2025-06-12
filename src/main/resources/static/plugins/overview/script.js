let overviewEventSource = null;
let overviewPlot = {};
let overviewData = {};

function initPluginOverview() {
    const metrics = ["cpu", "mem", "disk", "temp"];
    overviewData = {};
    overviewPlot = {};

    for (const key of metrics) {
        const el = document.getElementById(`overview-graph-${key}`);
        if (!el) {
            continue;
        }

        overviewData[key] = [[], []];

        overviewPlot[key] = new uPlot({
            title: key.toUpperCase(),
            width: 300,
            height: 200,
            series: [
                {},
                {
                    label: key,
                    stroke: "blue",
                }
            ],
            axes: [
                {
                    values: (u, vals) => vals.map(v => new Date(v).toLocaleTimeString())
                },
                {}
            ]
        }, overviewData[key], el);
    }

    overviewEventSource = new EventSource("/plugin/overview/graph");

    overviewEventSource.onopen = () => console.log("SSE connected");
    overviewEventSource.onerror = e => console.error("SSE error", e);

    overviewEventSource.onmessage = (e) => {
        const { ts, ...rest } = JSON.parse(e.data);

        for (const key of Object.keys(overviewPlot)) {
            const val = rest[key];
            console.log(`→ ${key}:`, val);

            if (typeof val !== "number" || isNaN(val)) {
                continue;
            }

            const data = overviewData[key];
            data[0].push(ts);
            data[1].push(val);

            if (data[0].length > 60) {
                data[0].shift();
                data[1].shift();
            }

            overviewPlot[key].setData(data);
        }
    };
}

function deinitPluginOverview() {
    if (overviewEventSource) {
        overviewEventSource.close();
        overviewEventSource = null;
    }
    overviewPlot = {};
    overviewData = {};
}
