<template>
  <view class="page-equip-detail" v-if="equip">
    <view class="header">
      <image
        class="equip-avatar"
        :src="equip.imageUrl || '/static/images/default.png'"
        mode="aspectFit"
      />
      <view class="equip-info">
        <text class="equip-name">{{ equip.name }}</text>
        <text class="equip-id">ID: {{ equip.id }}</text>
        <text class="equip-cat">{{ equip.category }}</text>
      </view>
    </view>

    <view class="section card" v-if="equip.description">
      <view class="section-title">装备描述</view>
      <text class="desc-text">{{ equip.description }}</text>
    </view>

    <view class="section card" v-if="equip.effect">
      <view class="section-title">效果说明</view>
      <text class="desc-text">{{ equip.effect }}</text>
    </view>

    <view class="section card" v-if="equip.obtainMethod">
      <view class="section-title">获取方式</view>
      <text class="desc-text">{{ equip.obtainMethod }}</text>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { get } from '@/utils/request'

const equip = ref<any>(null)

onLoad(async (options: any) => {
  if (options.id) {
    try {
      const res = await get<any>(`/api/equipment/${options.id}`)
      equip.value = res.data
    } catch (e) {
      console.error(e)
      uni.showToast({ title: '加载失败', icon: 'none' })
    }
  }
})
</script>

<style lang="scss" scoped>
.page-equip-detail {
  min-height: 100vh;
  background: #f5f5f5;
}

.header {
  background: linear-gradient(135deg, #f39c12 0%, #e67e22 100%);
  padding: 40rpx 30rpx;
  display: flex;
  align-items: center;
}

.equip-avatar {
  width: 140rpx;
  height: 140rpx;
  border-radius: 20rpx;
  background: rgba(255, 255, 255, 0.2);
}

.equip-info {
  margin-left: 30rpx;
  flex: 1;
}

.equip-name {
  font-size: 36rpx;
  font-weight: bold;
  color: #fff;
  display: block;
}

.equip-id {
  font-size: 24rpx;
  color: rgba(255, 255, 255, 0.7);
  display: block;
  margin-top: 8rpx;
}

.equip-cat {
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
