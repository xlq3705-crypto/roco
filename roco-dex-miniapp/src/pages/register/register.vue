<template>
  <view class="page-register">
    <view class="logo-section">
      <view class="logo">&#x1F43E;</view>
      <text class="title">注册账号</text>
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
        <text class="label">昵称</text>
        <input
          class="input"
          type="text"
          v-model="form.nickname"
          placeholder="请输入昵称（选填）"
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
      <view class="input-group">
        <text class="label">确认密码</text>
        <input
          class="input"
          type="password"
          v-model="confirmPassword"
          placeholder="请再次输入密码"
          maxlength="20"
        />
      </view>

      <button class="btn-primary" :loading="loading" @tap="onRegister">注 册</button>

      <view class="footer">
        <text class="link" @tap="goLogin">已有账号？立即登录</text>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { post } from '@/utils/request'

const loading = ref(false)
const confirmPassword = ref('')
const form = reactive({
  username: '',
  password: '',
  nickname: ''
})

async function onRegister() {
  if (!form.username.trim() || !form.password.trim()) {
    uni.showToast({ title: '请输入用户名和密码', icon: 'none' })
    return
  }
  if (form.password !== confirmPassword.value) {
    uni.showToast({ title: '两次密码不一致', icon: 'none' })
    return
  }
  if (form.password.length < 6) {
    uni.showToast({ title: '密码至少6位', icon: 'none' })
    return
  }
  loading.value = true
  try {
    const res = await post<any>('/api/auth/register', form)
    uni.setStorageSync('token', res.data.token)
    uni.setStorageSync('userInfo', JSON.stringify(res.data))
    uni.showToast({ title: '注册成功', icon: 'success' })
    setTimeout(() => {
      uni.navigateBack({ delta: 2 })
    }, 1000)
  } catch (e: any) {
    uni.showToast({ title: e.message || '注册失败', icon: 'none' })
  } finally {
    loading.value = false
  }
}

function goLogin() {
  uni.navigateBack()
}
</script>

<style lang="scss" scoped>
.page-register {
  min-height: 100vh;
  background: linear-gradient(135deg, #4a90d9 0%, #357abd 100%);
  display: flex;
  flex-direction: column;
  align-items: center;
  padding-top: 120rpx;
}

.logo-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 60rpx;
}

.logo {
  font-size: 80rpx;
  margin-bottom: 16rpx;
}

.title {
  font-size: 40rpx;
  font-weight: bold;
  color: #fff;
}

.form-section {
  width: 85%;
  background: #fff;
  border-radius: 24rpx;
  padding: 50rpx 40rpx;
}

.input-group {
  margin-bottom: 24rpx;
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
