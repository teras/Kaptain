let overviewEventSource = null;
let overviewPlot = {};
let overviewData = {};

function debounce(fn, delay = 100) {
    let timeout;
    return (...args) => {
        clearTimeout(timeout);
        timeout = setTimeout(() => fn(...args), delay);
    };
}

function observeResize(container, plot, fixedHeight = 200) {
    const resize = debounce(() => {
        plot.setSize({
            width: container.clientWidth,
            height: fixedHeight
        });
    });

    resize(); // initial size
    new ResizeObserver(resize).observe(container);
}

function createPlot(container, title, series, data, height = 300) {
    const plot = new uPlot({
        title,
        width: container.clientWidth,
        height: height,
        series,
        axes: [
            {
                values: (u, vals) => vals.map(v => new Date(v).toLocaleTimeString())
            },
            {}
        ],
        legend: { show: false }
    }, data, container);

    observeResize(container, plot, height);
    return plot;
}

function updateMultiPlot(containerId, key, entries, valueKey, labelKey, ts) {
    const el = document.getElementById(containerId);
    if (!overviewPlot[key]) {
        const labels = entries.map(e => e[labelKey]);
        overviewData[key] = {
            ts: [ts],
            series: Object.fromEntries(labels.map(name => [name, [entries.find(e => e[labelKey] === name)?.[valueKey] || 0]]))
        };

        const dataArr = [
            overviewData[key].ts,
            ...labels.map(name => overviewData[key].series[name])
        ];

        const seriesConfig = [{}, ...labels.map((name, i) => ({
            label: name,
            stroke: `hsl(${i * 15 % 360}, 70%, 50%)`,
            width: 1
        }))];

        overviewPlot[key] = createPlot(el, key.toUpperCase(), seriesConfig, dataArr);
    } else {
        overviewData[key].ts.push(ts);
        for (const e of entries) {
            overviewData[key].series[e[labelKey]].push(e[valueKey]);
        }

        if (overviewData[key].ts.length > 60) {
            overviewData[key].ts.shift();
            for (const name in overviewData[key].series) {
                overviewData[key].series[name].shift();
            }
        }

        const updatedData = [
            overviewData[key].ts,
            ...Object.keys(overviewData[key].series).map(name => overviewData[key].series[name])
        ];
        overviewPlot[key].setData(updatedData);
    }
}

function initPluginOverview() {
    const metrics = ["mem", "disk"];

    for (const key of metrics) {
        const el = document.getElementById(`overview-graph-${key}`);
        if (!el) continue;

        overviewData[key] = [[], []];
        const series = [{}, { label: key, stroke: "blue" }];
        overviewPlot[key] = createPlot(el, key.toUpperCase(), series, overviewData[key]);
    }

    overviewEventSource = new EventSource("/plugin/overview/graph");
    overviewEventSource.onopen = () => console.log("SSE connected");
    overviewEventSource.onerror = e => console.error("SSE error", e);

    overviewEventSource.onmessage = (e) => {
        const { ts, cpu, mem, disk, temp } = JSON.parse(e.data);

        for (const key of ["mem", "disk"]) {
            const val = eval(key);
            if (typeof val !== "number") continue;

            const data = overviewData[key];
            data[0].push(ts);
            data[1].push(val);

            if (data[0].length > 60) {
                data[0].shift();
                data[1].shift();
            }

            overviewPlot[key].setData(data);
        }

        if (Array.isArray(cpu)) {
            updateMultiPlot("overview-graph-cpu", "cpu", cpu, "usage", "name", ts);
        }

        if (Array.isArray(temp)) {
            updateMultiPlot("overview-graph-temp", "temp", temp, "temp", "name", ts);
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
