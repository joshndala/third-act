<template>
  <header :class="{ scrolled, 'dark-hero': isDarkHero }">
    <nav>
      <RouterLink to="/" class="nav-logo">
        <img src="/icon.png" alt="The Third Act" />
        <span>The Third Act</span>
      </RouterLink>
      <div class="nav-links">
        <RouterLink to="/">Home</RouterLink>
        <RouterLink to="/why">Why</RouterLink>
        <RouterLink to="/setup">Setup</RouterLink>
        <a v-if="isSupported" href="https://github.com/joshndala/third-act/releases/latest/download/TheThirdAct-macOS.dmg" class="btn-primary nav-cta">Download</a>
        <button v-else class="btn-primary nav-cta is-disabled" title="Currently exclusive to macOS desktop">Mac Required</button>
      </div>
      <!-- Mobile toggle -->
      <button class="menu-toggle" @click="open = !open" :aria-expanded="open">
        <span /><span /><span />
      </button>
    </nav>
    <div class="mobile-menu" :class="{ open }">
      <RouterLink to="/" @click="open = false">Home</RouterLink>
      <RouterLink to="/why" @click="open = false">Why</RouterLink>
      <RouterLink to="/setup" @click="open = false">Setup</RouterLink>
      <a v-if="isSupported" href="https://github.com/joshndala/third-act/releases/latest/download/TheThirdAct-macOS.dmg" class="btn-primary">Download</a>
      <button v-else class="btn-primary is-disabled">Mac Required</button>
    </div>
  </header>
</template>

<script setup>
import { ref, onMounted, onUnmounted, computed } from 'vue'
import { useRoute } from 'vue-router'
import { usePlatformDetection } from '../composables/usePlatformDetection'

const route = useRoute()
const { isSupported } = usePlatformDetection()
const scrolled = ref(false)
const open = ref(false)

const isHome = computed(() => route.path === '/')
const isDarkHero = computed(() => isHome.value && !scrolled.value)

function onScroll() { scrolled.value = window.scrollY > 40 }
onMounted(() => window.addEventListener('scroll', onScroll))
onUnmounted(() => window.removeEventListener('scroll', onScroll))
</script>

<style scoped>
header {
  position: fixed;
  top: 0; left: 0; right: 0;
  z-index: 100;
  padding: 0 5%;
  transition: background 0.3s, box-shadow 0.3s;
}
header.scrolled {
  background: rgba(245, 242, 233, 0.95);
  backdrop-filter: blur(12px);
  box-shadow: 0 1px 0 rgba(43,51,88,0.08);
}
nav {
  display: flex;
  align-items: center;
  height: 72px;
  gap: 40px;
}
.nav-logo {
  display: flex;
  align-items: center;
  gap: 10px;
  text-decoration: none;
  flex-shrink: 0;
}
.nav-logo img {
  height: 40px;
  width: 40px;
  object-fit: contain;
}
.nav-logo span {
  font-family: var(--font-serif);
  font-size: 1.1rem;
  font-weight: 700;
  color: var(--navy);
}
.nav-links {
  display: flex;
  align-items: center;
  gap: 32px;
  margin-left: auto;
}
.nav-links a {
  font-size: 0.95rem;
  font-weight: 500;
  color: var(--navy);
  text-decoration: none;
  transition: color 0.2s;
}
.nav-links a:hover,
.nav-links a.router-link-active:not(.nav-cta) {
  color: var(--gold);
}
.nav-cta {
  padding: 10px 24px !important;
  font-size: 0.9rem !important;
}
.is-disabled {
  opacity: 0.5;
  cursor: not-allowed !important;
  pointer-events: none;
}
.menu-toggle {
  display: none;
  flex-direction: column;
  gap: 5px;
  background: none;
  border: none;
  cursor: pointer;
  margin-left: auto;
  padding: 4px;
}
.menu-toggle span {
  display: block;
  width: 24px;
  height: 2px;
  background: var(--navy);
  border-radius: 2px;
  transition: all 0.2s;
}
.mobile-menu {
  display: none;
  flex-direction: column;
  gap: 16px;
  padding: 20px 0;
  background: var(--cream);
}
.mobile-menu a {
  font-size: 1.1rem;
  font-weight: 500;
  color: var(--navy);
  text-decoration: none;
}
header.dark-hero .nav-links a:not(.nav-cta),
header.dark-hero .nav-logo span {
  color: var(--cream);
}
header.dark-hero .menu-toggle span {
  background: var(--cream);
}
@media (max-width: 768px) {
  .nav-links { display: none; }
  .menu-toggle { display: flex; }
  .mobile-menu.open { display: flex; }
  header.scrolled .mobile-menu.open { background: rgba(245,242,233,0.97); }
}
</style>
