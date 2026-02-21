<template>
  <div id="app-wrapper">
    <NavBar />
    <main>
      <RouterView />
    </main>
    <FooterBar />
  </div>
</template>

<script setup>
import NavBar from './components/NavBar.vue'
import FooterBar from './components/FooterBar.vue'
</script>

<style>
#app-wrapper {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
}
main { flex: 1; }
</style>
