<template>
  <view class="page-pet-detail" v-if="pet">
    <!-- 宠物头部信息 -->
    <view class="header">
      <image
        class="pet-avatar"
        :src="pet.imageUrl || '/static/images/default.png'"
        mode="aspectFit"
      />
      <view class="pet-info">
        <text class="pet-name">{{ pet.name }}</text>
        <text class="pet-no">编号: {{ pet.petNo }}</text>
        <view class="pet-attrs">
          <attr-badge :attr="pet.mainAttr" />
          <attr-badge v-if="pet.subAttr" :attr="pet.subAttr" />
        </view>
      </view>
    </view>

    <!-- 基础属性 -->
    <view class="section card">
      <view class="section-title">基础属性</view>
      <view class="stats-table">
        <view class="stat-row" v-for="stat in stats" :key="stat.label">
          <text class="stat-label">{{ stat.label }}</text>
          <view class="stat-bar-wrap">
            <view class="stat-bar" :style="{ width: (stat.value / 255 * 100) + '%', background: stat.color }" />
          </view>
          <text class="stat-value">{{ stat.value }}</text>
        </view>
      </view>
    </view>

    <!-- 简介 -->
    <view class="section card" v-if="pet.description">
      <view class="section-title">简介</view>
      <text class="desc-text">{{ pet.description }}</text>
    </view>

    <!-- 获取方式 -->
    <view class="section card" v-if="pet.obtainMethod">
      <view class="section-title">获取方式</view>
      <text class="desc-text">{{ pet.obtainMethod }}</text>
    </view>

    <!-- 技能列表 -->
    <view class="section card" v-if="pet.skills && pet.skills.length > 0">
      <view class="section-title">可学技能</view>
      <view class="skill-list">
        <view
          v-for="skill in pet.skills"
          :key="skill.id"
          class="skill-item"
          @tap="goSkillDetail(skill.id)"
        >
          <view class="skill-left">
            <text class="skill-name">{{ skill.name }}</text>
            <view class="skill-meta">
              <attr-badge :attr="skill.attr" />
              <text class="skill-type">{{ skill.type }}</text>
            </view>
          </view>
          <view class="skill-right">
            <text class="skill-power" v-if="skill.power">威力: {{ skill.power }}</text>
            <text class="skill-pp">PP: {{ skill.pp }}</text>
          </view>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { get } from '@/utils/request'
import AttrBadge from '@/components/attr-badge/attr-badge.vue'

const pet = ref<any>(null)

const stats = computed(() => {
  if (!pet.value) return []
  return [
    { label: '精力', value: pet.value.hp, color: '#e74c3c' },
    { label: '攻击', value: pet.value.attack, color: '#f39c12' },
    { label: '防御', value: pet.value.defense, color: '#3498db' },
    { label: '魔攻', value: pet.value.spAttack, color: '#9b59b6' },
    { label: '魔防', value: pet.value.spDefense, color: '#27ae60' },
    { label: '速度', value: pet.value.speed, color: '#1abc9c' }
  ]
})

onLoad(async (options: any) => {
  if (options.id) {
    const res = await get<any>(`/api/pet/${options.id}`)
    pet.value = res.data
  }
})

function goSkillDetail(id: number) {
  uni.navigateTo({ url: `/pages/skill-detail/skill-detail?id=${id}` })
}
</script>

<style lang="scss" scoped>
.page-pet-detail {
  min-height: 100vh;
  background: #f5f5f5;
}

.header {
  background: linear-gradient(135deg, #4a90d9 0%, #357abd 100%);
  padding: 40rpx 30rpx;
  display: flex;
  align-items: center;
}

.pet-avatar {
  width: 160rpx;
  height: 160rpx;
  border-radius: 24rpx;
  background: rgba(255, 255, 255, 0.2);
}

.pet-info {
  margin-left: 30rpx;
  flex: 1;
}

.pet-name {
  font-size: 40rpx;
  font-weight: bold;
  color: #fff;
  display: block;
}

.pet-no {
  font-size: 26rpx;
  color: rgba(255, 255, 255, 0.8);
  display: block;
  margin: 8rpx 0;
}

.pet-attrs {
  display: flex;
  gap: 12rpx;
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
  margin-bottom: 20rpx;
}

.stats-table {
  width: 100%;
}

.stat-row {
  display: flex;
  align-items: center;
  margin-bottom: 16rpx;
}

.stat-label {
  width: 80rpx;
  font-size: 26rpx;
  color: #666;
}

.stat-bar-wrap {
  flex: 1;
  height: 20rpx;
  background: #f0f0f0;
  border-radius: 10rpx;
  margin: 0 16rpx;
  overflow: hidden;
}

.stat-bar {
  height: 100%;
  border-radius: 10rpx;
  transition: width 0.3s;
}

.stat-value {
  width: 60rpx;
  text-align: right;
  font-size: 26rpx;
  color: #333;
  font-weight: bold;
}

.desc-text {
  font-size: 28rpx;
  color: #666;
  line-height: 1.6;
}

.skill-list {
  width: 100%;
}

.skill-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16rpx 0;
  border-bottom: 1rpx solid #f0f0f0;

  &:last-child {
    border-bottom: none;
  }
}

.skill-left {
  flex: 1;
}

.skill-name {
  font-size: 28rpx;
  color: #333;
  display: block;
}

.skill-meta {
  display: flex;
  align-items: center;
  gap: 12rpx;
  margin-top: 8rpx;
}

.skill-type {
  font-size: 24rpx;
  color: #999;
}

.skill-right {
  text-align: right;
}

.skill-power, .skill-pp {
  font-size: 24rpx;
  color: #666;
  display: block;
}
</style>
