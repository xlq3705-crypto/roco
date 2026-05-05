<template>
  <view class="page-forgot">
    <view class="logo-section">
      <view class="logo">🐾</view>
      <text class="title">找回密码</text>
      <text class="subtitle">通过用户名和昵称验证身份</text>
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
          placeholder="请输入注册时的昵称"
          maxlength="20"
        />
      </view>
      <view class="input-group">
        <text class="label">新密码</text>
        <input
          class="input"
          type="password"
          v-model="form.newPassword"
          placeholder="请输入新密码"
          maxlength="20"
        />
      </view>

      <button class="btn-primary" :loading="loading" @tap="onReset">重置密码</button>

      <view class="footer">
        <text class="link" @tap="goBack">返回登录</text>
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
  nickname: '',
  newPassword: ''
})

async function onReset() {
  if (!form.username.trim() || !form.nickname.trim() || !form.newPassword.trim()) {
    uni.showToast({ title: '请填写完整信息', icon: 'none' })
    return
  }
  if (form.newPassword.length < 6) {
    uni.showToast({ title: '密码至少6位', icon: 'none' })
    return
  }
  loading.value = true
  try {
    await post('/api/auth/reset-password', form)
    uni.showToast({ title: '密码重置成功', icon: 'success' })
    setTimeout(() => {
      uni.navigateBack()
    }, 1000)
  } catch (e: any) {
    uni.showToast({ title: e.message || '重置失败', icon: 'none' })
  } finally {
    loading.value = false
  }
}

function goBack() {
  uni.navigateBack()
}
</script>

<style lang="scss" scoped>
.page-forgot {
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
