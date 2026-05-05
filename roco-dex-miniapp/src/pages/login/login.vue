<template>
  <view class="page-login">
    <view class="logo-section">
      <view class="logo">🐾</view>
      <text class="title">洛克王国图鉴</text>
      <text class="subtitle">登录后享受更多功能</text>
    </view>

    <view class="form-section">
      <view class="input-group">
        <text class="label">用户名</text>
        <input
          class="input"
          type="text"
          v-model="form.username"
          placeholder="请输入用户名"
          maxlength="20"
        />
      </view>
      <view class="input-group">
        <text class="label">密码</text>
        <input
          class="input"
          type="password"
          v-model="form.password"
          placeholder="请输入密码"
          maxlength="20"
        />
      </view>

      <button class="btn-primary" :loading="loading" @tap="onLogin">登 录</button>

      <view class="footer">
        <text class="link" @tap="goRegister">还没有账号？立即注册</text>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { post } from '@/utils/request'

const loading = ref(false)
const form = reactive({
  username: '',
  password: ''
})

async function onLogin() {
  if (!form.username.trim() || !form.password.trim()) {
    uni.showToast({ title: '请输入用户名和密码', icon: 'none' })
    return
  }
  loading.value = true
  try {
    const res = await post<any>('/api/auth/login', form)
    uni.setStorageSync('token', res.data.token)
    uni.setStorageSync('userInfo', JSON.stringify(res.data))
    uni.showToast({ title: '登录成功', icon: 'success' })
    setTimeout(() => {
      uni.navigateBack()
    }, 1000)
  } catch (e: any) {
    uni.showToast({ title: e.message || '登录失败', icon: 'none' })
  } finally {
    loading.value = false
  }
}

function goRegister() {
  uni.navigateTo({ url: '/pages/register/register' })
}
</script>

<style lang="scss" scoped>
.page-login {
  min-height: 100vh;
  background: linear-gradient(135deg, #4a90d9 0%, #357abd 100%);
  display: flex;
  flex-direction: column;
  align-items: center;
  padding-top: 160rpx;
}

.logo-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 80rpx;
}

.logo {
  font-size: 100rpx;
  margin-bottom: 20rpx;
}

.title {
  font-size: 44rpx;
  font-weight: bold;
  color: #fff;
}

.subtitle {
  font-size: 26rpx;
  color: rgba(255, 255, 255, 0.7);
  margin-top: 12rpx;
}

.form-section {
  width: 85%;
  background: #fff;
  border-radius: 24rpx;
  padding: 50rpx 40rpx;
}

.input-group {
  margin-bottom: 30rpx;
}

.label {
  font-size: 28rpx;
  color: #333;
  display: block;
  margin-bottom: 12rpx;
}

.input {
  width: 100%;
  height: 88rpx;
  background: #f5f5f5;
  border-radius: 12rpx;
  padding: 0 24rpx;
  font-size: 28rpx;
  box-sizing: border-box;
}

.btn-primary {
  width: 100%;
  height: 88rpx;
  background: #4a90d9;
  color: #fff;
  font-size: 32rpx;
  border-radius: 12rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-top: 20rpx;
  border: none;
}

.footer {
  text-align: center;
  margin-top: 30rpx;
}

.link {
  font-size: 26rpx;
  color: #4a90d9;
}
</style>
