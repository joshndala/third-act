<template>
  <!-- ══ HERO ══════════════════════════════════════════════════════════ -->
  <section class="hero">
    <div class="hero-inner">
      <img src="/icon.png" alt="The Third Act logo" class="hero-logo" />
      <p class="section-label">A Mac App for Movie Lovers</p>
      <h1>Your films deserve<br /><em>more than a star rating.</em></h1>
      <p class="hero-sub">
        The Third Act is a private, cinematic movie journal that lives entirely
        on your computer — no algorithms, no social feeds, just you and your
        relationship with film.
      </p>
      <div class="hero-cta">
        <a v-if="isSupported" href="https://github.com/joshndala/third-act/releases/latest/download/TheThirdAct-macOS.dmg" class="btn-primary">
          ↓ Download for Mac
        </a>
        <button v-else class="btn-primary is-disabled" title="Exclusive to macOS desktop">
          macOS Required to Install
        </button>
        <RouterLink to="/why" class="btn-ghost">Why it exists →</RouterLink>
      </div>
      <p class="hero-platform-note">Currently exclusive to macOS</p>
    </div>
    <div class="hero-grain"></div>
  </section>

  <!-- ══ FEATURES ═══════════════════════════════════════════════════════ -->
  <section class="features-section">
    <div class="section">
      <p class="section-label" style="text-align:center">What's inside</p>
      <h2 style="text-align:center;margin-bottom:60px">Built for how you<br />actually feel about movies</h2>
      <div class="features-grid">
        <div class="feature-card feature-card--1">
          <div class="feature-icon">🔒</div>
          <h3>Truly Private</h3>
          <p>
            Your journal lives in a local database on your Mac. No cloud, no
            account, no one reading your takes. It's yours, full stop.
          </p>
        </div>
        <div class="feature-card feature-card--2">
          <div class="feature-icon">🎬</div>
          <h3>Meaningful Entries</h3>
          <p>
            Four purposeful fields — The Summary, The Vibe, The Peak Moment,
            Extra Notes — push you to reflect, not just log.
          </p>
        </div>
        <div class="feature-card feature-card--3">
          <div class="feature-icon">✨</div>
          <h3>AI-Powered Archiving</h3>
          <p>
            Already wrote your thoughts on paper? Snap a photo and let AI
            transcribe your handwritten notes directly into the journal —
            word for word.
          </p>
        </div>
        <div class="feature-card feature-card--4">
          <div class="feature-icon">🔓</div>
          <h3>Fully Open Source</h3>
          <p>
            The Third Act is completely open source. You can see, download, and edit the code however you want through our <a href="https://github.com/joshndala/third-act" target="_blank" rel="noopener" class="text-link">GitHub repo</a>. For more details, verify the <RouterLink to="/setup" class="text-link">Setup page</RouterLink>.
          </p>
        </div>
      </div>
    </div>
  </section>

  <!-- ══ PULL QUOTE ═════════════════════════════════════════════════════ -->
  <section class="quote-section">
    <div class="section">
      <blockquote>
        <span class="quote-mark">"</span>
        A film isn't something that happens to you. It's something you carry.
        <span class="quote-mark">"</span>
      </blockquote>
      <p class="quote-sub">The Third Act is where you put it down on paper.</p>
      <a v-if="isSupported" href="https://github.com/joshndala/third-act/releases/latest/download/TheThirdAct-macOS.dmg" class="btn-primary">Get started in 5 minutes →</a>
      <button v-else class="btn-primary is-disabled">Exclusive to macOS</button>
    </div>
  </section>
</template>

<script setup>
import { usePlatformDetection } from '../composables/usePlatformDetection'
const { isSupported } = usePlatformDetection()
</script>

<style scoped>
/* ── Hero ───────────────────────────────────────────── */
.hero {
  position: relative;
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--navy-deep);
  overflow: hidden;
  padding: 120px 5% 80px;
}
.hero-grain {
  position: absolute;
  inset: 0;
  background-image: url("data:image/svg+xml,%3Csvg viewBox='0 0 256 256' xmlns='http://www.w3.org/2000/svg'%3E%3Cfilter id='noise'%3E%3CfeTurbulence type='fractalNoise' baseFrequency='0.9' numOctaves='4' stitchTiles='stitch'/%3E%3C/filter%3E%3Crect width='100%25' height='100%25' filter='url(%23noise)' opacity='0.04'/%3E%3C/svg%3E");
  pointer-events: none;
}
.hero-inner {
  position: relative;
  z-index: 1;
  max-width: 720px;
  text-align: center;
}
.hero-logo {
  width: 110px;
  height: 110px;
  object-fit: contain;
  margin-bottom: 28px;
  filter: drop-shadow(0 8px 24px rgba(212,161,92,0.3));
  animation: float 4s ease-in-out infinite;
}
@keyframes float {
  0%, 100% { transform: translateY(0); }
  50%       { transform: translateY(-10px); }
}
.hero .section-label {
  margin-bottom: 16px;
}
.hero h1 {
  font-size: clamp(2.6rem, 6vw, 4.2rem);
  color: var(--cream);
  margin-bottom: 24px;
}
.hero h1 em {
  color: var(--gold);
  font-style: italic;
}
.hero-sub {
  font-size: 1.15rem;
  color: rgba(245,242,233,0.85);
  max-width: 560px;
  margin: 0 auto 44px;
  line-height: 1.75;
}
.hero-cta {
  display: flex;
  gap: 16px;
  justify-content: center;
  flex-wrap: wrap;
}
.is-disabled {
  opacity: 0.5;
  cursor: not-allowed !important;
  pointer-events: none;
}
.hero .btn-ghost {
  color: var(--cream);
  border-color: rgba(245,242,233,0.4);
}
.hero .btn-ghost:hover {
  background: rgba(245,242,233,0.1);
  color: var(--cream);
  border-color: var(--cream);
}
.hero-platform-note {
  font-size: 0.8rem;
  color: rgba(245,242,233,0.6);
  margin-top: 20px;
  font-family: var(--font-sans);
  letter-spacing: 0.05em;
  text-transform: uppercase;
}

/* ── Features ───────────────────────────────────────── */
.features-section {
  background: var(--cream);
  padding: 100px 0;
}
.features-section h2 {
  font-size: clamp(1.8rem, 3.5vw, 2.6rem);
}
.features-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 28px;
}
@media (max-width: 768px) {
  .features-grid {
    grid-template-columns: 1fr;
    gap: 20px;
  }
}
.feature-card {
  border-radius: var(--card-radius);
  padding: 40px 32px;
  border: 1px solid rgba(43,51,88,0.08);
  transition: transform 0.3s var(--ease), box-shadow 0.3s var(--ease);
}
.feature-card:hover {
  transform: translateY(-6px);
  box-shadow: 0 20px 50px rgba(43,51,88,0.1);
}
.feature-card--1 { background: #EDE9DC; }
.feature-card--2 { background: var(--navy); }
.feature-card--2 h3,
.feature-card--2 p {
  color: var(--cream);
}
.feature-card--2 p { opacity: 0.8; }
.feature-card--3 { background: var(--gold); }
.feature-card--3 h3,
.feature-card--3 p {
  color: var(--navy-deep);
}
.feature-card--4 { background: #EDE9DC; }
.feature-icon {
  font-size: 2rem;
  margin-bottom: 16px;
}
.feature-card h3 {
  font-size: 1.3rem;
  margin-bottom: 12px;
}
.feature-card p {
  font-size: 0.97rem;
  line-height: 1.7;
  color: var(--ink-muted);
}

.text-link {
  color: inherit;
  text-decoration: underline;
  text-decoration-color: rgba(43,51,88,0.3);
  transition: text-decoration-color 0.2s ease;
  font-weight: 500;
}

.text-link:hover {
  text-decoration-color: var(--navy-deep);
}

/* ── Quote ──────────────────────────────────────────── */
.quote-section {
  background: var(--gold);
  padding: 100px 0;
  text-align: center;
}
.quote-section .section {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 24px;
}
blockquote {
  font-family: var(--font-serif);
  font-size: clamp(1.5rem, 3vw, 2.2rem);
  font-style: italic;
  color: var(--navy-deep);
  max-width: 700px;
  line-height: 1.5;
}
.quote-mark {
  font-size: 3rem;
  line-height: 0;
  vertical-align: -0.35em;
  color: var(--navy);
  opacity: 0.3;
}
.quote-sub {
  font-size: 1.05rem;
  color: var(--navy-deep);
  opacity: 0.8;
}
.quote-section .btn-primary {
  background: var(--navy);
  color: var(--cream);
}
.quote-section .btn-primary:hover {
  background: var(--navy-deep);
  color: var(--cream);
}
</style>
