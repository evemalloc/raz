<script setup>
import { RouterLink, RouterView } from 'vue-router'
</script>

<template>
  <header>
    <nav>
      <RouterLink to="/">Home</RouterLink>
      <RouterLink to="/about">About</RouterLink>
      <RouterLink to="/media">Media</RouterLink>
    </nav>
  </header>

  <RouterView />
</template>

<style scoped>
header {
  position: fixed;
  top: 0;
  left: 0;
  z-index: 1000;
  padding: 1rem;
  background-color: white;
}

nav {
  text-align: left;
}

nav a.router-link-exact-active {
  color: var(--color-text);
}

nav a.router-link-exact-active:hover {
  background-color: transparent;
}

nav a {
  display: inline-block;
  padding: 0 1rem;
  border-left: 1px solid var(--color-border);
}

nav a:first-of-type {
  border: 0;
}
</style>

<style scoped>
header {
  position: fixed;
  top: 0;
  left: 0;
  z-index: 1000;
  padding: 1rem;
  background-color: white;
}

nav {
  text-align: left;
}

nav a.router-link-exact-active {
  color: var(--color-text);
}

nav a.router-link-exact-active:hover {
  background-color: transparent;
}

nav a {
  display: inline-block;
  padding: 0 1rem;
  border-left: 1px solid var(--color-border);
}

nav a:first-of-type {
  border: 0;
}
#app {
  padding-top: 60px;
}
</style>
