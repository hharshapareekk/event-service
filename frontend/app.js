const BASE_URL = "https://friendly-parakeet-66wv9gpr7g734wj5-8080.app.github.dev";

// CREATE EVENT
async function createEvent() {
    const data = {
        bundleId: parseInt(document.getElementById("bundleId").value),
        sourceType: document.getElementById("sourceType").value,
        userName: document.getElementById("userName").value,
        severity: parseInt(document.getElementById("severity").value),
        message: document.getElementById("message").value,
        tsUtc: new Date().toISOString()
    };

    await fetch(BASE_URL + "/events", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(data)
    });

    alert("Event Created!");
    loadEvents();
}

// LOAD EVENTS
async function loadEvents() {
    const res = await fetch(BASE_URL + "/events");
    const data = await res.json();

    const table = document.getElementById("eventsTable");

    table.innerHTML = `
        <tr>
            <th>ID</th>
            <th>Bundle</th>
            <th>Source</th>
            <th>User</th>
            <th>Severity</th>
            <th>Message</th>
        </tr>
    `;

    data.forEach(e => {
        table.innerHTML += `
            <tr>
                <td>${e.id}</td>
                <td>${e.bundleId}</td>
                <td>${e.sourceType}</td>
                <td>${e.userName}</td>
                <td>${e.severity}</td>
                <td>${e.message}</td>
            </tr>
        `;
    });
}

// LOAD TIMELINE
async function loadTimeline() {
    const bundleId = document.getElementById("timelineBundleId").value;

    const res = await fetch(BASE_URL + "/events/timeline?bundleId=" + bundleId);
    const data = await res.json();

    const table = document.getElementById("timelineTable");

    table.innerHTML = `
        <tr>
            <th>ID</th>
            <th>Time</th>
            <th>Source</th>
            <th>Message</th>
        </tr>
    `;

    data.forEach(e => {
        table.innerHTML += `
            <tr>
                <td>${e.id}</td>
                <td>${e.tsUtc}</td>
                <td>${e.sourceType}</td>
                <td>${e.message}</td>
            </tr>
        `;
    });
}