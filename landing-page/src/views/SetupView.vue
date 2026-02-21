<template>
  <!-- ══ HERO ══════════════════════════════════════════════════════════════ -->
  <section class="setup-hero">
    <div class="section">
      <p class="section-label">The Docs</p>
      <div class="divider"></div>
      <h1>Up and running<br /><em>in about 5 minutes.</em></h1>
      <p class="setup-hero-sub">
        The Third Act is a local Mac app — no subscription, no login, no cloud.
        It does need two API keys to unlock everything. Here's exactly how.
      </p>
    </div>
  </section>

  <!-- ══ PREREQUISITES ═════════════════════════════════════════════════════ -->
  <section class="prereqs-section">
    <div class="section prereqs-inner">
      <div class="prereqs-text">
        <p class="section-label">Before you start</p>
        <div class="divider"></div>
        <h2>What you'll need</h2>
        <ul class="prereq-list">
          <li>
            <span class="check">✓</span>
            <div>
              <strong>A Mac</strong>
              <span>macOS 11 Big Sur or later. Windows support is on the roadmap.</span>
            </div>
          </li>
          <li>
            <span class="check">✓</span>
            <div>
              <strong>Java 19+</strong>
              <span>The app is built with JavaFX. Download from
                <a href="https://adoptium.net" target="_blank">Adoptium</a>
                if you don't have it.
              </span>
            </div>
          </li>
          <li>
            <span class="check">✓</span>
            <div>
              <strong>~5 minutes</strong>
              <span>That's genuinely all it takes.</span>
            </div>
          </li>
        </ul>
      </div>
      <div class="prereqs-image-wrap">
        <img
          src="/man-confused-with-wires.webp"
          alt="Don't worry, it's not this complicated"
        />
        <p class="caption">It will not look like this. I promise.</p>
      </div>
    </div>
  </section>

  <!-- ══ STEP 1 — DOWNLOAD ═════════════════════════════════════════════════ -->
  <section class="step-section step-alt">
    <div class="section">
      <div class="step-header">
        <span class="step-badge">Step 1</span>
        <h2>Download the app</h2>
      </div>
      <p class="step-desc">
        Grab the latest release from GitHub. Download the <code>.dmg</code> file
        and install it like any other Mac app.
      </p>
      <a
        href="https://github.com/joshndala/third-act/releases/latest/download/TheThirdAct-macOS.dmg"
        class="btn-primary"
      >Download for Mac ↓</a>
    </div>
  </section>

  <!-- ══ STEP 2 — TMDB ═════════════════════════════════════════════════════ -->
  <section class="step-section">
    <div class="section">
      <div class="step-header">
        <span class="step-badge">Step 2</span>
        <h2>Get your TMDb API Key <span class="required-tag">Required</span></h2>
      </div>
      <p class="step-desc">
        The app uses The Movie Database (TMDb) to fetch movie details, posters,
        and backdrops. It's completely free.
      </p>
      <div class="steps-list">
        <div class="step-item">
          <span class="step-num">1</span>
          <p>
            Create a free account at
            <a href="https://www.themoviedb.org/signup" target="_blank">themoviedb.org</a>.
          </p>
        </div>
        <div class="step-item">
          <span class="step-num">2</span>
          <p>
            Go to your account's
            <a href="https://www.themoviedb.org/settings/api" target="_blank">API settings</a>
            and request an API key (choose "Developer").
          </p>
        </div>
        <div class="step-item">
          <span class="step-num">3</span>
          <p>
            Open The Third Act, click the <strong>⚙ Settings</strong> gear icon in
            the top right, paste your key into the <strong>"TMDb API Key"</strong>
            field, and hit <strong>Save Keys</strong>.
          </p>
        </div>
      </div>
    </div>
  </section>

  <!-- ══ STEP 3 — GEMINI ═══════════════════════════════════════════════════ -->
  <section class="step-section step-alt gemini-section">
    <div class="section">
      <div class="step-header">
        <span class="step-badge step-badge--gold">Step 3</span>
        <h2>Get your Gemini API Key <span class="optional-tag">Optional but awesome</span></h2>
      </div>
      <p class="step-desc">
        This unlocks the star feature: snap a photo of your handwritten movie
        notes and Gemini will transcribe them directly into your journal entry —
        word for word.
      </p>
      <div class="steps-list">
        <div class="step-item">
          <span class="step-num step-num--gold">1</span>
          <p>
            Visit
            <a href="https://aistudio.google.com/apikey" target="_blank">Google AI Studio</a>
            and sign in with your Google account.
          </p>
        </div>
        <div class="step-item">
          <span class="step-num step-num--gold">2</span>
          <p>
            Click <strong>"Create API key"</strong> — it's free to generate.
          </p>
        </div>
        <div class="step-item">
          <span class="step-num step-num--gold">3</span>
          <p>
            In the app, open <strong>⚙ Settings</strong>, paste your key into
            the <strong>"Gemini API Key"</strong> field, and save. Done.
          </p>
        </div>
      </div>
      <div class="gemini-tip">
        <span>💡</span>
        <p>
          Once set up, open any journal entry, tap
          <strong>"📎 Import from Handwritten Notes"</strong>, select your
          photo or PDF, and watch it fill in automatically. You can still
          edit everything before saving.
        </p>
      </div>
    </div>
  </section>

  <!-- ══ DONE ═══════════════════════════════════════════════════════════════ -->
  <section class="done-section">
    <div class="section" style="text-align:center">
      <div class="done-icon">🎬</div>
      <h2>That's it. You're ready.</h2>
      <p class="done-sub">
        Search for a film, fill in your thoughts, and start building a journal
        that's actually worth re-reading.
      </p>
      <a href="https://github.com/joshndala/third-act/releases/latest/download/TheThirdAct-macOS.dmg" class="btn-primary">
        ↓ Download The Third Act
      </a>
    </div>
  </section>
</template>

<script setup></script>

<style scoped>
/* ── Hero ───────────────────────────────────────────── */
.setup-hero {
  padding: 140px 5% 80px;
  background: var(--cream);
}
.setup-hero h1 {
  font-size: clamp(2.4rem, 5vw, 3.6rem);
  margin-bottom: 24px;
}
.setup-hero h1 em { color: var(--gold); font-style: italic; }
.setup-hero-sub {
  font-size: 1.1rem;
  color: var(--ink-muted);
  max-width: 560px;
  line-height: 1.75;
}

/* ── Prerequisites ──────────────────────────────────── */
.prereqs-section {
  background: var(--navy-deep);
  padding: 100px 0;
}
.prereqs-inner {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 60px;
  align-items: center;
}
.prereqs-text .section-label { color: var(--gold); }
.prereqs-text .divider { background: var(--gold); }
.prereqs-text h2 {
  color: var(--cream);
  font-size: clamp(1.8rem, 3vw, 2.4rem);
  margin-bottom: 28px;
}
.prereq-list {
  list-style: none;
  display: flex;
  flex-direction: column;
  gap: 20px;
}
.prereq-list li {
  display: flex;
  gap: 14px;
  align-items: flex-start;
}
.check {
  color: var(--gold);
  font-size: 1.1rem;
  flex-shrink: 0;
  margin-top: 2px;
}
.prereq-list strong {
  display: block;
  color: var(--cream);
  margin-bottom: 2px;
}
.prereq-list span {
  color: rgba(245,242,233,0.6);
  font-size: 0.9rem;
  line-height: 1.6;
}
.prereq-list a { color: var(--gold); }

/* Portrait image — constrain width so it doesn't dominate */
.prereqs-image-wrap {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
}
.prereqs-image-wrap img {
  width: 100%;
  max-width: 300px;
  border-radius: var(--card-radius);
  border: 3px solid rgba(212,161,92,0.25);
  box-shadow: 0 24px 60px rgba(0,0,0,0.5);
  object-fit: cover;
}
.caption {
  font-size: 0.8rem;
  font-style: italic;
  color: rgba(245,242,233,0.35);
}
@media (max-width: 768px) {
  .prereqs-inner { grid-template-columns: 1fr; }
  .prereqs-image-wrap { order: -1; }
}

/* ── Steps ──────────────────────────────────────────── */
.step-section { padding: 80px 0; background: var(--cream); }
.step-section.step-alt { background: var(--cream-dark); }
.step-header {
  display: flex;
  align-items: center;
  gap: 16px;
  flex-wrap: wrap;
  margin-bottom: 16px;
}
.step-header h2 {
  font-size: clamp(1.5rem, 2.8vw, 2rem);
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
}
.step-badge {
  background: var(--navy);
  color: var(--cream);
  font-family: var(--font-sans);
  font-size: 0.75rem;
  font-weight: 700;
  letter-spacing: 0.1em;
  text-transform: uppercase;
  padding: 6px 14px;
  border-radius: 50px;
  flex-shrink: 0;
}
.step-badge--gold {
  background: var(--gold);
  color: var(--navy-deep);
}
.required-tag {
  font-size: 0.75rem;
  font-family: var(--font-sans);
  font-weight: 600;
  background: rgba(43,51,88,0.1);
  color: var(--navy);
  padding: 4px 10px;
  border-radius: 50px;
}
.optional-tag {
  font-size: 0.75rem;
  font-family: var(--font-sans);
  font-weight: 600;
  background: rgba(212,161,92,0.2);
  color: var(--gold);
  padding: 4px 10px;
  border-radius: 50px;
}
.step-desc {
  color: var(--ink-muted);
  font-size: 1rem;
  line-height: 1.75;
  max-width: 620px;
  margin-bottom: 32px;
}
.steps-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
  margin-bottom: 32px;
}
.step-item {
  display: flex;
  gap: 16px;
  align-items: flex-start;
}
.step-num {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: var(--navy);
  color: var(--cream);
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 700;
  font-size: 0.85rem;
  flex-shrink: 0;
  margin-top: 2px;
}
.step-num--gold { background: var(--gold); color: var(--navy-deep); }
.step-item p {
  color: var(--ink);
  font-size: 0.97rem;
  line-height: 1.7;
}
.step-item code {
  background: rgba(43,51,88,0.1);
  padding: 2px 6px;
  border-radius: 4px;
  font-size: 0.88em;
  color: var(--navy);
}

/* ── Gemini section ─────────────────────────────────── */
.gemini-section.step-alt { background: var(--navy); }
.gemini-section .step-desc,
.gemini-section .step-item p { color: rgba(245,242,233,0.7); }
.gemini-section .step-header h2 { color: var(--cream); }
.gemini-section .step-item strong { color: var(--cream); }
.gemini-tip {
  display: flex;
  gap: 14px;
  background: rgba(212,161,92,0.12);
  border: 1px solid rgba(212,161,92,0.25);
  border-radius: var(--card-radius);
  padding: 20px 24px;
  margin-top: 8px;
  max-width: 680px;
}
.gemini-tip span { font-size: 1.4rem; flex-shrink: 0; margin-top: 2px; }
.gemini-tip p {
  color: rgba(245,242,233,0.75);
  font-size: 0.95rem;
  line-height: 1.7;
}
.gemini-tip strong { color: var(--gold); }

/* ── Done ───────────────────────────────────────────── */
.done-section {
  background: var(--gold);
  padding: 100px 0;
}
.done-icon { font-size: 3rem; margin-bottom: 16px; }
.done-section h2 {
  font-size: clamp(1.8rem, 3.5vw, 2.6rem);
  color: var(--navy-deep);
  margin-bottom: 16px;
}
.done-sub {
  color: var(--navy-deep);
  opacity: 0.75;
  font-size: 1.05rem;
  max-width: 500px;
  margin: 0 auto 36px;
}
.done-section .btn-primary {
  background: var(--navy);
  color: var(--cream);
}
.done-section .btn-primary:hover {
  background: var(--navy-deep);
  color: var(--cream);
}
</style>
