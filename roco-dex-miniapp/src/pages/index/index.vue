<template>
  <view class="page-index">
    <!-- 用户信息栏 -->
    <view class="user-bar" @tap="onUserTap">
      <view class="user-avatar">{{ userInfo ? userInfo.nickname[0] : '&#x1F464;' }}</view>
      <text class="user-name">{{ userInfo ? userInfo.nickname : '点击登录' }}</text>
    </view>

    <!-- 搜索栏 -->
    <view class="search-section">
      <view class="search-wrap" @tap="goSearch">
        <text class="search-icon">&#x1F50D;</text>
        <text class="search-placeholder">搜索宠物、技能、道具...</text>
      </view>
    </view>

    <!-- 分类宫格 -->
    <view class="section">
      <view class="section-title">资料查询</view>
      <view class="category-grid">
        <view class="category-item" @tap="navigateTo('/pages/pet-list/pet-list')">
          <view class="category-icon" style="background: #4a90d9;">&#x1F43E;</view>
          <text>宠物大全</text>
        </view>
        <view class="category-item" @tap="navigateTo('/pages/skill-list/skill-list')">
          <view class="category-icon" style="background: #e74c3c;">&#x26A1;</view>
          <text>技能大全</text>
        </view>
        <view class="category-item" @tap="navigateTo('/pages/item-list/item-list')">
          <view class="category-icon" style="background: #27ae60;">&#x1F4E6;</view>
          <text>道具大全</text>
        </view>
        <view class="category-item" @tap="navigateTo('/pages/equipment-list/equipment-list')">
          <view class="category-icon" style="background: #f39c12;">&#x2694;</view>
          <text>装备大全</text>
        </view>
      </view>
    </view>

    <!-- 最新公告 -->
    <view class="section">
      <view class="section-title">最新公告</view>
      <view class="card">
        <view class="news-item">
          <text class="news-tag">公告</text>
          <text class="news-text">欢迎使用洛克王国图鉴查询小程序！</text>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'

const userInfo = ref<any>(null)

onShow(() => {
  const stored = uni.getStorageSync('userInfo')
  if (stored) {
    userInfo.value = JSON.parse(stored)
  } else {
    userInfo.value = null
  }
})

function onUserTap() {
  if (!userInfo.value) {
    uni.navigateTo({ url: '/pages/login/login' })
  }
}

function navigateTo(url: string) {
  uni.navigateTo({ url })
}

function goSearch() {
  uni.navigateTo({ url: '/pages/pet-list/pet-list?focus=true' })
}
</script>

<style lang="scss" scoped>
.page-index {
  min-height: 100vh;
  background: #f5f5f5;
}

.user-bar {
  display: flex;
  align-items: center;
  padding: 20rpx 30rpx;
  background: #fff;
}

.user-avatar {
  width: 64rpx;
  height: 64rpx;
  border-radius: 50%;
  background: #4a90d9;
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28rpx;
  margin-right: 16rpx;
}

.user-name {
  font-size: 28rpx;
  color: #333;
}

.search-section {
  background: linear-gradient(135deg, #4a90d9 0%, #357abd 100%);
  padding: 40rpx 30rpx 30rpx;
}

.search-wrap {
  display: flex;
  align-items: center;
  background: rgba(255, 255, 255, 0.9);
  border-radius: 36rpx;
  padding: 16rpx 24rpx;
}

.search-icon {
  font-size: 28rpx;
  margin-right: 12rpx;
}

.search-placeholder {
  color: #999;
  font-size: 28rpx;
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

.category-grid {
  display: flex;
  justify-content: space-around;
  padding: 0 20rpx;
}

.category-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 20rpx;

  text {
    font-size: 24rpx;
    color: #666;
    margin-top: 12rpx;
  }
}

.category-icon {
  width: 100rpx;
  height: 100rpx;
  border-radius: 24rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 48rpx;
}

.card {
  margin: 0 24rpx;
  background: #fff;
  border-radius: 16rpx;
  padding: 24rpx;
}

.news-item {
  display: flex;
  align-items: center;
}

.news-tag {
  background: #4a90d9;
  color: #fff;
  font-size: 22rpx;
  padding: 4rpx 12rpx;
  border-radius: 6rpx;
  margin-right: 16rpx;
}

.news-text {
  font-size: 28rpx;
  color: #333;
  flex: 1;
}
</style>
