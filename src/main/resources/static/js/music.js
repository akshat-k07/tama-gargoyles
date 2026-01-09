(() => {
  const KEY = "musicOn";

  function audio() { return document.getElementById("bg-music"); }
  function track() { return document.body.getAttribute("data-track"); }

  function setSrc() {
    const a = audio(), t = track();
    if (!a || !t) return;
    if (a.dataset.src !== t) {
      a.src = t;
      a.dataset.src = t;
      a.load();
    }
  }

  function setButtonState() {
    const btn = document.getElementById("music-toggle");
    if (!btn) return;
    btn.textContent = localStorage.getItem(KEY) === "1" ? "⏸ Music" : "▶ Music";
  }

  async function tryPlay() {
    if (localStorage.getItem(KEY) !== "1") return;
    const a = audio();
    if (!a) return;
    try {
      await a.play();
      removeBanner();
    } catch (e) {
      showBanner(); // autoplay blocked
    }
  }

  function showBanner() {
    if (document.getElementById("sound-banner")) return;

    const div = document.createElement("div");
    div.id = "sound-banner";
    div.style.position = "fixed";
    div.style.bottom = "12px";
    div.style.left = "12px";
    div.style.padding = "10px 12px";
    div.style.background = "rgba(0,0,0,0.7)";
    div.style.color = "white";
    div.style.borderRadius = "10px";
    div.style.zIndex = "9999";
    div.style.cursor = "pointer";
    div.textContent = "Tap to enable sound";

    div.addEventListener("click", async () => {
      localStorage.setItem(KEY, "1");
      setSrc();
      await tryPlay();
      setButtonState();
    });

    document.body.appendChild(div);
  }

  function removeBanner() {
    const b = document.getElementById("sound-banner");
    if (b) b.remove();
  }

  // Homepage toggle (one button)
  window.tgToggleMusic = async () => {
    const a = audio();
    if (!a) return;

    if (localStorage.getItem(KEY) === "1") {
      localStorage.setItem(KEY, "0");
      a.pause();
      removeBanner();
    } else {
      localStorage.setItem(KEY, "1");
      setSrc();
      await tryPlay();
    }
    setButtonState();
  };

  document.addEventListener("DOMContentLoaded", async () => {
    setSrc();
    setButtonState();
    await tryPlay();
  });
})();
