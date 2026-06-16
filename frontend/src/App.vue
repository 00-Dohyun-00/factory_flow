<template>
  <Login v-if="route.name === 'Login'" />
  <Register v-else-if="route.name === 'Register'" />
  <Layout v-else />
</template>

<script setup lang="ts">
import { useRoute } from 'vue-router'
import Layout from '@/components/Layout.vue'
import Login from '@/views/Login.vue'
import Register from '@/views/Register.vue'

const route = useRoute()
</script>

<style>
@import '@/assets/styles/main.css';
</style>
