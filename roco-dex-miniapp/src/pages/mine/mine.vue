<template>
  <view class="page-mine">
    <!-- 未登录状态 -->
    <view v-if="!userInfo" class="login-prompt">
      <view class="avatar-placeholder">?</view>
      <button class="btn-login" @tap="onLoginTap">点击登录</button>
    </view>

    <!-- 已登录状态 -->
    <view v-else class="profile-section">
      <view class="profile-header">
        <view class="avatar">{{ userInfo.nickname?.[0] || '?' }}</view>
        <view class="profile-info">
          <text class="nickname">{{ userInfo.nickname }}</text>
          <text class="username">账号: {{ userInfo.username }}</text>
        </view>
      </view>
    </view>

    <!-- 我的图鉴收集 -->
    <view class="section">
      <view class="section-title">我的图鉴收集</view>
      <view class="collection-list">
        <view class="collection-item" @tap="onCollectionTap('世界图鉴')">
          <view class="collection-icon" style="background: #4a90d9;">🌍</view>
          <text class="collection-name">世界图鉴</text>
          <text class="collection-count">0/100</text>
        </view>
        <view class="collection-item" @tap="onCollectionTap('风眠省图鉴')">
          <view class="collection-icon" style="background: #27ae60;">🏔</view>
          <text class="collection-name">风眠省图鉴</text>
          <text class="collection-count">0/80</text>
        </view>
        <view class="collection-item" @tap="onCollectionTap('洛克里安图鉴')">
          <view class="collection-icon" style="background: #e74c3c;">📖</view>
          <text class="collection-name">洛克里安图鉴</text>
          <text class="collection-count">0/60</text>
        </view>
      </view>
    </view>

    <!-- 退出登录 -->
    <view v-if="userInfo" class="logout-section">
      <button class="btn-logout" @tap="onLogout">退出登录</button>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'

const userInfo = ref<any>(null)

onShow(() => {
  const stored = uni.getStorageSync('userInfo')
  userInfo.value = stored ? JSON.parse(stored) : null
})

function onLoginTap() {
  uni.navigateTo({ url: '/pages/login/login' })
}

function onCollectionTap(name: string) {
  uni.showToast({ title: '功能开发中', icon: 'none' })
}

function onLogout() {
  uni.showModal({
    title: '提示',
    content: '确定退出登录吗？',
    success: (res) => {
      if (res.confirm) {
        uni.removeStorageSync('token')
        uni.removeStorageSync('userInfo')
        userInfo.value = null
        uni.showToast({ title: '已退出', icon: 'success' })
      }
    }
  })
}
</script>

<style lang="scss" scoped>
.page-mine {
  min-height: 100vh;
  background: #f5f5f5;
}

.login-prompt {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 80rpx 0 40rpx;
  background: #fff;
}

.avatar-placeholder {
  width: 120rpx;
  height: 120rpx;
  border-radius: 50%;
  background: #e0e0e0;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 48rpx;
  color: #999;
  margin-bottom: 24rpx;
}

.btn-login {
  background: #4a90d9;
  color: #fff;
  font-size: 28rpx;
  padding: 16rpx 60rpx;
  border-radius: 40rpx;
  border: none;
}

.profile-section {
  background: linear-gradient(135deg, #4a90d9 0%, #357abd 100%);
  padding: 60rpx 30rpx 40rpx;
}

.profile-header {
  display: flex;
  align-items: center;
}

.avatar {
  width: 120rpx;
  height: 120rpx;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.3);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 48rpx;
  font-weight: bold;
  margin-right: 30rpx;
}

.profile-info {
  flex: 1;
}

.nickname {
  font-size: 36rpx;
  font-weight: bold;
  color: #fff;
  display: block;
}

.username {
  font-size: 26rpx;
  color: rgba(255, 255, 255, 0.7);
  display: block;
  margin-top: 8rpx;
}

.section {
  margin: 24rpx 0;
}

.section-title {
  font-size: 32rpx;
  font-weight: bold;
  color: #333;
  padding: 0 30rpx;
  margin-bottom: 20rpx;
}

.collection-list {
  padding: 0 24rpx;
}

.collection-item {
  display: flex;
  align-items: center;
  background: #fff;
  border-radius: 16rpx;
  padding: 24rpx;
  margin-bottom: 16rpx;
}

.collection-icon {
  width: 80rpx;
  height: 80rpx;
  border-radius: 20rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 40rpx;
  margin-right: 24rpx;
}

.collection-name {
  flex: 1;
  font-size: 30rpx;
  color: #333;
}

.collection-count {
  font-size: 26rpx;
  color: #999;
}

.logout-section {
  padding: 40rpx 24rpx;
}

.btn-logout {
  width: 100%;
  height: 88rpx;
  background: #fff;
  color: #e74c3c;
  font-size: 30rpx;
  border-radius: 16rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 1rpx solid #e74c3c;
}
</style>
