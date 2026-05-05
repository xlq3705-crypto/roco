<template>
  <view class="page-skill-detail" v-if="skill">
    <view class="header">
      <view class="skill-title">
        <text class="name">{{ skill.name }}</text>
        <attr-badge :attr="skill.attr" />
      </view>
    </view>

    <view class="section card">
      <view class="info-grid">
        <view class="info-item">
          <text class="info-label">属性</text>
          <text class="info-value">{{ skill.attr }}</text>
        </view>
        <view class="info-item">
          <text class="info-label">类型</text>
          <text class="info-value">{{ skill.type }}</text>
        </view>
        <view class="info-item">
          <text class="info-label">威力</text>
          <text class="info-value">{{ skill.power || '--' }}</text>
        </view>
        <view class="info-item">
          <text class="info-label">PP</text>
          <text class="info-value">{{ skill.pp }}</text>
        </view>
        <view class="info-item" v-if="skill.accuracy">
          <text class="info-label">命中</text>
          <text class="info-value">{{ skill.accuracy }}%</text>
        </view>
      </view>
    </view>

    <view class="section card" v-if="skill.description">
      <view class="section-title">效果描述</view>
      <text class="desc-text">{{ skill.description }}</text>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { get } from '@/utils/request'
import AttrBadge from '@/components/attr-badge/attr-badge.vue'

const skill = ref<any>(null)

onLoad(async (options: any) => {
  if (options.id) {
    try {
      const res = await get<any>(`/api/skill/${options.id}`)
      skill.value = res.data
    } catch (e) {
      console.error(e)
      uni.showToast({ title: '加载失败', icon: 'none' })
    }
  }
})
</script>

<style lang="scss" scoped>
.page-skill-detail {
  min-height: 100vh;
  background: #f5f5f5;
}

.header {
  background: linear-gradient(135deg, #e74c3c 0%, #c0392b 100%);
  padding: 40rpx 30rpx;
}

.skill-title {
  display: flex;
  align-items: center;
  gap: 16rpx;
}

.name {
  font-size: 40rpx;
  font-weight: bold;
  color: #fff;
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

.info-grid {
  display: flex;
  flex-wrap: wrap;
}

.info-item {
  width: 33.33%;
  text-align: center;
  padding: 16rpx 0;
}

.info-label {
  display: block;
  font-size: 24rpx;
  color: #999;
}

.info-value {
  display: block;
  font-size: 32rpx;
  font-weight: bold;
  color: #333;
  margin-top: 8rpx;
}

.desc-text {
  font-size: 28rpx;
  color: #666;
  line-height: 1.6;
}
</style>
