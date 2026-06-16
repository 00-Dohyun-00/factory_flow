<template>
  <div class="login-page">
    <div class="login-box">
      <h1 class="login-title">FactoryFlow</h1>
      <p class="login-subtitle">회원가입</p>

      <form @submit.prevent="handleRegister" class="login-form">
        <div class="form-group">
          <label class="form-label">아이디</label>
          <input
            v-model="username"
            type="text"
            class="input"
            placeholder="아이디를 입력하세요"
            required
          />
        </div>
        <div class="form-group">
          <label class="form-label">비밀번호</label>
          <input
            v-model="password"
            type="password"
            class="input"
            placeholder="비밀번호를 입력하세요"
            required
          />
        </div>
        <div class="form-group">
          <label class="form-label">비밀번호 확인</label>
          <input
            v-model="passwordConfirm"
            type="password"
            class="input"
            placeholder="비밀번호를 다시 입력하세요"
            required
          />
        </div>

        <p v-if="errorMessage" class="error-message">{{ errorMessage }}</p>
        <p v-if="successMessage" class="success-message">{{ successMessage }}</p>

        <button type="submit" class="btn btn-primary login-btn" :disabled="loading">
          {{ loading ? '처리 중...' : '회원가입' }}
        </button>

        <button type="button" class="btn btn-secondary login-btn" @click="router.push('/login')">
          로그인으로 돌아가기
        </button>
      </form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const username = ref('')
const password = ref('')
const passwordConfirm = ref('')
const errorMessage = ref('')
const successMessage = ref('')
const loading = ref(false)

const handleRegister = async () => {
  errorMessage.value = ''
  successMessage.value = ''

  if (password.value !== passwordConfirm.value) {
    errorMessage.value = '비밀번호가 일치하지 않습니다.'
    return
  }

  loading.value = true
  try {
    const res = await fetch(`${import.meta.env.VITE_API_URL}/api/auth/register`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ username: username.value, password: password.value })
    })

    if (!res.ok) {
      const data = await res.json()
      throw new Error(data.message || '회원가입에 실패했습니다.')
    }

    successMessage.value = '회원가입이 완료됐습니다. 로그인 해주세요.'
    setTimeout(() => router.push('/login'), 1500)
  } catch (e: any) {
    errorMessage.value = e.message
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: var(--bg-secondary);
}

.login-box {
  background: white;
  padding: 2.5rem;
  border-radius: 0.75rem;
  box-shadow: var(--shadow-lg);
  width: 100%;
  max-width: 400px;
}

.login-title {
  font-size: 1.75rem;
  font-weight: 700;
  color: var(--primary-color);
  text-align: center;
  margin-bottom: 0.25rem;
}

.login-subtitle {
  font-size: 0.875rem;
  color: var(--text-secondary);
  text-align: center;
  margin-bottom: 2rem;
}

.login-form {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.form-label {
  font-size: 0.875rem;
  font-weight: 500;
}

.login-btn {
  width: 100%;
  padding: 0.75rem;
  margin-top: 0.5rem;
  font-size: 1rem;
}

.error-message {
  color: var(--error-color);
  font-size: 0.875rem;
  text-align: center;
}

.success-message {
  color: var(--success-color);
  font-size: 0.875rem;
  text-align: center;
}
</style>
