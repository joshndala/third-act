<template>
  <!-- ══ HERO ══════════════════════════════════════════════════════════════ -->
  <section class="why-hero">
    <div class="section">
      <p class="section-label">The Philosophy</p>
      <div class="divider"></div>
      <h1>Stop logging.<br /><em>Start remembering.</em></h1>
      <p class="why-hero-sub">
        There's nothing wrong with Letterboxd. But somewhere between the four-star
        averages and the follower counts, the movie itself got lost.
      </p>
    </div>
  </section>

  <!-- ══ THE ANTI-LOG ══════════════════════════════════════════════════════ -->
  <section class="antilog-section">
    <div class="section antilog-inner">
      <div class="antilog-text">
        <p class="section-label">The Problem</p>
        <div class="divider"></div>
        <h2>Modern movie tracking<br />is a scoreboard.</h2>
        <p>
          Apps today turn your taste into a number — a tally, a percentage, a
          badge. You finish a film, drop a rating, and move on. The watch count
          climbs. The meaning evaporates.
        </p>
        <p>
          We optimise for <em>more</em> when we should be asking
          <em>why this one mattered</em>.
        </p>
      </div>
      <div class="antilog-image-wrap">
        <img src="/shia-watching-movies.gif" alt="Shia LaBeouf watching movies with emotion" />
        <p class="caption">This is what we're protecting.</p>
      </div>
    </div>
  </section>

  <!-- ══ THE FOUR PILLARS ══════════════════════════════════════════════════ -->
  <section class="pillars-section">
    <div class="section">
      <p class="section-label" style="text-align:center">The Anatomy of an Entry</p>
      <div class="divider" style="margin:16px auto 16px"></div>
      <h2 style="text-align:center;margin-bottom:16px">Four questions.<br />One film. Full clarity.</h2>
      <p class="pillars-intro">
        Every Third Act entry is built around four purposeful prompts — each one
        designed to pull a different layer of meaning out of your experience.
      </p>
      <div class="pillars-grid">
        <div class="pillar-card pillar-1">
          <div class="pillar-number">01</div>
          <h3>The Summary</h3>
          <p class="pillar-prompt">"How would you describe it to a friend at a bar?"</p>
          <p>
            No Rotten Tomatoes prose. Just your honest, off-the-cuff read of
            the film in plain language. The kind of thing you'd actually say.
          </p>
        </div>
        <div class="pillar-card pillar-2">
          <div class="pillar-number">02</div>
          <h3>The Vibe</h3>
          <p class="pillar-prompt">"What did it feel like to sit in that world?"</p>
          <p>
            Atmospheric. Emotional. Sensory. This is the mood you carried out of
            the cinema — the texture of the experience, not its plot.
          </p>
        </div>
        <div class="pillar-card pillar-3">
          <div class="pillar-number">03</div>
          <h3>The Peak Moment</h3>
          <p class="pillar-prompt">"The one scene you'll still think about next year."</p>
          <p>
            Every great film has a pivot — a frame, a line, a cut — that
            crystallises everything. Name it. Pin it. Don't let it disappear.
          </p>
        </div>
        <div class="pillar-card pillar-4">
          <div class="pillar-number">04</div>
          <h3>Extra Notes</h3>
          <p class="pillar-prompt">"Everything else you don't want to forget."</p>
          <p>
            Who you saw it with. A piece of trivia from the credits. A random
            association it sparked. This is your margin — use it.
          </p>
        </div>
      </div>
    </div>
  </section>

  <!-- ══ CTA ═══════════════════════════════════════════════════════════════ -->
  <section class="why-cta">
    <div class="section" style="text-align:center">
      <h2>Ready to actually remember<br />what you watched?</h2>
      <div class="why-cta-btns">
        <a v-if="isSupported" href="https://github.com/joshndala/third-act/releases/latest/download/TheThirdAct-macOS.dmg" class="btn-primary">Download for Mac</a>
        <button v-else class="btn-primary is-disabled">Exclusive to macOS</button>
        <RouterLink to="/setup" class="btn-ghost">See the setup guide →</RouterLink>
      </div>
    </div>
  </section>
</template>

<script setup>
import { usePlatformDetection } from '../composables/usePlatformDetection'
const { isSupported } = usePlatformDetection()
</script>

<style scoped>
/* ── Hero ───────────────────────────────────────────── */
.why-hero {
  padding: 140px 5% 80px;
  background: var(--cream);
}
.why-hero h1 {
  font-size: clamp(2.4rem, 5vw, 3.8rem);
  margin-bottom: 24px;
}
.why-hero h1 em { color: var(--gold); font-style: italic; }
.why-hero-sub {
  font-size: 1.1rem;
  color: var(--ink-muted);
  max-width: 580px;
  line-height: 1.75;
}

/* ── Anti-log ───────────────────────────────────────── */
.antilog-section {
  background: var(--navy-deep);
  padding: 100px 0;
}
.antilog-inner {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 60px;
  align-items: center;
}
.antilog-text .section-label { color: var(--gold); }
.antilog-text .divider { background: var(--gold); }
.antilog-text h2 {
  font-size: clamp(1.8rem, 3.5vw, 2.6rem);
  color: var(--cream);
  margin-bottom: 20px;
}
.antilog-text p {
  color: rgba(245,242,233,0.85);
  font-size: 1rem;
  line-height: 1.8;
  margin-bottom: 16px;
}
.antilog-text em { color: var(--gold); font-style: normal; font-weight: 600; }
.antilog-image-wrap {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
}
.antilog-image-wrap img {
  width: 100%;
  max-width: 420px;
  border-radius: var(--card-radius);
  border: 3px solid rgba(212,161,92,0.3);
  box-shadow: 0 24px 60px rgba(0,0,0,0.5);
}
.caption {
  font-size: 0.8rem;
  color: rgba(245,242,233,0.35) !important;
  font-style: italic;
  opacity: 1 !important;
}
@media (max-width: 768px) {
  .antilog-inner { grid-template-columns: 1fr; }
  .antilog-image-wrap { order: -1; }
}

/* ── Pillars ─────────────────────────────────────────── */
.pillars-section {
  background: var(--cream);
  padding: 100px 0;
}
.pillars-section h2 {
  font-size: clamp(1.8rem, 3.5vw, 2.6rem);
}
.pillars-intro {
  text-align: center;
  color: var(--ink-muted);
  max-width: 560px;
  margin: 0 auto 60px;
  font-size: 1rem;
  line-height: 1.75;
}
.pillars-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 32px;
}
@media (max-width: 768px) {
  .pillars-grid {
    grid-template-columns: 1fr;
    gap: 20px;
  }
}
.pillar-card {
  border-radius: var(--card-radius);
  padding: 36px 28px;
  border: 1px solid rgba(43,51,88,0.1);
  transition: transform 0.3s var(--ease), box-shadow 0.3s var(--ease);
  position: relative;
  overflow: hidden;
}
.pillar-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 20px 50px rgba(43,51,88,0.1);
}
.pillar-1 { background: #EDE9DC; }
.pillar-2 { background: var(--navy); }
.pillar-2 h3, .pillar-2 p { color: var(--cream); }
.pillar-2 .pillar-prompt { color: var(--gold); }
.pillar-2 p:last-child { opacity: 0.7; }
.pillar-3 { background: var(--gold); }
.pillar-3 h3, .pillar-3 p { color: var(--navy-deep); }
.pillar-4 { background: #EDE9DC; }
.pillar-number {
  font-family: var(--font-serif);
  font-size: 3.5rem;
  font-weight: 700;
  color: var(--gold);
  opacity: 0.2;
  position: absolute;
  top: 12px;
  right: 20px;
  line-height: 1;
}
.pillar-3 .pillar-number { color: var(--navy); }
.pillar-2 .pillar-number { color: var(--cream); }
.pillar-card h3 {
  font-size: 1.25rem;
  margin-bottom: 8px;
  position: relative;
}
.pillar-prompt {
  font-size: 0.85rem;
  font-style: italic;
  color: var(--gold);
  margin-bottom: 14px;
  position: relative;
}
.pillar-card p:last-child {
  font-size: 0.95rem;
  line-height: 1.7;
  color: var(--ink-muted);
  position: relative;
}

/* ── CTA ─────────────────────────────────────────────── */
.why-cta {
  background: var(--navy);
  padding: 100px 0;
}
.why-cta h2 {
  color: var(--cream);
  font-size: clamp(1.8rem, 3.5vw, 2.6rem);
}
.why-cta-btns {
  display: flex;
  gap: 16px;
  justify-content: center;
  flex-wrap: wrap;
  margin-top: 36px;
}
.is-disabled {
  opacity: 0.5;
  cursor: not-allowed !important;
  pointer-events: none;
}
.why-cta .btn-ghost {
  color: var(--cream);
  border-color: rgba(245,242,233,0.4);
}
.why-cta .btn-ghost:hover {
  background: rgba(245,242,233,0.1);
  color: var(--cream);
}
</style>
