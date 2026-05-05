<template>
  <view class="page-item-detail" v-if="item">
    <view class="header">
      <image
        class="item-avatar"
        :src="item.imageUrl || '/static/images/default.png'"
        mode="aspectFit"
      />
      <view class="item-info">
        <text class="item-name">{{ item.name }}</text>
        <text class="item-id">ID: {{ item.id }}</text>
        <text class="item-cat">{{ item.category }}</text>
      </view>
    </view>

    <view class="section card" v-if="item.description">
      <view class="section-title">道具描述</view>
      <text class="desc-text">{{ item.description }}</text>
    </view>

    <view class="section card" v-if="item.obtainMethod">
      <view class="section-title">获取方式</view>
      <text class="desc-text">{{ item.obtainMethod }}</text>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { get } from '@/utils/request'

const item = ref<any>(null)

onLoad(async (options: any) => {
  if (options.id) {
    const res = await get<any>(`/api/item/${options.id}`)
    item.value = res.data
  }
})
</script>

<style lang="scss" scoped>
.page-item-detail {
  min-height: 100vh;
  background: #f5f5f5;
}

.header {
  background: linear-gradient(135deg, #27ae60 0%, #219a52 100%);
  padding: 40rpx 30rpx;
  display: flex;
  align-items: center;
}

.item-avatar {
  width: 140rpx;
  height: 140rpx;
  border-radius: 20rpx;
  background: rgba(255, 255, 255, 0.2);
}

.item-info {
  margin-left: 30rpx;
  flex: 1;
}

.item-name {
  font-size: 36rpx;
  font-weight: bold;
  color: #fff;
  display: block;
}

.item-id {
  font-size: 24rpx;
  color: rgba(255, 255, 255, 0.7);
  display: block;
  margin-top: 8rpx;
}

.item-cat {
  font-size: 24rpx;
  color: rgba(255, 255, 255, 0.9);
  display: block;
  margin-top: 8rpx;
}

.section {
  margin: 20rpx 24rpx;
}

.card {
  background: #fff;
  border-radius: 16rpx;
  padding: 24rpx;
}

.section-title {
  font-size: 30rpx;
  font-weight: bold;
  color: #333;
  margin-bottom: 16rpx;
}

.desc-text {
  font-size: 28rpx;
  color: #666;
  line-height: 1.6;
}
</style>
