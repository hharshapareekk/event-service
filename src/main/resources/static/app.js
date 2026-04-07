function apiRoot() {
  const v = document.getElementById("apiBase").value.trim();
  return v.replace(/\/$/, "");
}

async function fetchJson(path) {
  const r = await fetch(apiRoot() + path);
  if (!r.ok) {
    const t = await r.text();
    throw new Error(t || r.statusText);
  }
  return r.json();
}

function renderBundles(list) {
  const ul = document.getElementById("bundleList");
  ul.innerHTML = "";
  list.forEach((b) => {
    const li = document.createElement("li");
    li.textContent = `id=${b.id}  key=${b.bundleKey} — ${b.name || ""}`;
    li.style.cursor = "pointer";
    li.title = "Use this bundle id";
    li.addEventListener("click", () => {
      document.getElementById("bundleId").value = b.id;
      loadTimeline().catch((e) => alert(e.message));
    });
    ul.appendChild(li);
  });
}

function escapeHtml(s) {
  return s.replace(/[&<>\"']/g, (c) => ({ "&": "&amp;", "<": "&lt;", ">": "&gt;", '"': "&quot;", "'": "&#39;" }[c]));
}

function renderTimeline(data) {
  window.__lastTimeline = data;
  const stats = document.getElementById("stats");
  const el = document.getElementById("timeline");
  const srcFilter = document.getElementById("srcFilter").value.trim().toLowerCase();
  const qFilter = document.getElementById("qFilter").value.trim().toLowerCase();

  let events = data.events || [];
  if (srcFilter) {
    events = events.filter((e) => (e.sourceType || "").toLowerCase().includes(srcFilter));
  }
  if (qFilter) {
    events = events.filter((e) => {
      const hay = `${e.message || ""} ${e.action || ""} ${e.objectValue || ""}`.toLowerCase();
      return hay.includes(qFilter);
    });
  }

  stats.textContent = `Bundle ${data.bundleKey || ""} — showing ${events.length} of ${
    (data.events || []).length
  } events · sources: ${(data.stats && data.stats.sources && data.stats.sources.join(", ")) || "—"}`;

  el.innerHTML = "";
  events.forEach((e) => {
    const card = document.createElement("article");
    card.className = "event-card";
    card.innerHTML = `
      <time>${escapeHtml(String(e.tsUtc || ""))}</time>
      <span class="badge">${escapeHtml(e.sourceType || "?")}</span>
      <span class="badge">sev ${e.severity ?? "—"}</span>
      <p>${escapeHtml(e.message || e.action || "")}</p>
    `;
    card.addEventListener("click", () => {
      document.getElementById("inspector").textContent = JSON.stringify(e, null, 2);
    });
    el.appendChild(card);
  });
}

async function loadTimeline() {
  const id = document.getElementById("bundleId").value;
  if (!id) return;
  const data = await fetchJson(`/timeline?bundleId=${encodeURIComponent(id)}`);
  renderTimeline(data);
}

function refilter() {
  if (window.__lastTimeline) {
    renderTimeline(window.__lastTimeline);
  }
}

async function loadBundles() {
  const list = await fetchJson("/bundles");
  renderBundles(list);
}

document.getElementById("btnBundles").addEventListener("click", () =>
  loadBundles().catch((e) => alert(e.message))
);
document.getElementById("btnTimeline").addEventListener("click", () =>
  loadTimeline().catch((e) => alert(e.message))
);
document.getElementById("srcFilter").addEventListener("input", refilter);
document.getElementById("qFilter").addEventListener("input", refilter);

// Initial load (best-effort)
loadBundles().catch(() => {});
loadTimeline().catch(() => {});

