<template>
  <footer>
    <div class="footer-inner">
      <div class="footer-brand">
        <img src="/icon.png" alt="The Third Act" />
        <div class="footer-brand-text">
          <p class="footer-tagline">Reflect. Record. Feel.</p>
          <p class="footer-platform">Designed for macOS</p>
        </div>
      </div>
      <div class="footer-links">
        <RouterLink to="/">Home</RouterLink>
        <RouterLink to="/why">Why</RouterLink>
        <RouterLink to="/setup">Setup</RouterLink>
        <a href="https://github.com/joshndala/third-act" target="_blank">GitHub</a>
      </div>
      <p class="footer-copy">© {{ year }} The Third Act</p>
    </div>
  </footer>
</template>

<script setup>
const year = new Date().getFullYear()
</script>

<style scoped>
footer {
  background: var(--navy-deep);
  color: var(--cream);
  padding: 48px 5%;
}
.footer-inner {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  align-items: center;
  gap: 40px;
  flex-wrap: wrap;
}
.footer-brand {
  display: flex;
  align-items: center;
  gap: 12px;
}
.footer-brand img {
  height: 36px;
  width: 36px;
  object-fit: contain;
  filter: brightness(1.1);
}
.footer-brand-text {
  display: flex;
  flex-direction: column;
}
.footer-tagline {
  font-family: var(--font-serif);
  font-style: italic;
  color: var(--cream);
  opacity: 0.7;
  margin: 0;
}
.footer-platform {
  font-size: 0.7rem;
  text-transform: uppercase;
  letter-spacing: 0.05em;
  opacity: 0.35;
  margin: 2px 0 0 0;
}
.footer-links {
  display: flex;
  gap: 28px;
  margin-left: auto;
}
.footer-links a {
  color: rgba(245,242,233,0.85);
  font-size: 0.9rem;
  text-decoration: none;
  transition: color 0.2s;
}
.footer-links a:hover { color: var(--gold); }
.footer-copy {
  width: 100%;
  text-align: center;
  font-size: 0.8rem;
  color: rgba(245,242,233,0.6);
  margin-top: 16px;
}
</style>
