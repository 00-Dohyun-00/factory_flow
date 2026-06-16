<template>
  <Login v-if="route.name === 'Login'" />
  <Layout v-else />
</template>

<script setup lang="ts">
import { useRoute } from 'vue-router'
import Layout from '@/components/Layout.vue'
import Login from '@/views/Login.vue'

const route = useRoute()
</script>

<style>
@import '@/assets/styles/main.css';
</style>
