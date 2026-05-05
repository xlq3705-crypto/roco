<template>
  <view class="attr-badge" :style="{ background: colorMap[attr] || '#999' }">
    <text>{{ attr }}</text>
  </view>
</template>

<script setup lang="ts">
defineProps<{
  attr: string
}>()

const colorMap: Record<string, string> = {
  '普通': '#a8a878',
  '火': '#f08030',
  '水': '#6890f0',
  '草': '#78c850',
  '电': '#f8d030',
  '冰': '#98d8d8',
  '翼': '#a890f0',
  '土': '#e0c068',
  '萌': '#ee99ac',
  '虫': '#a8b820',
  '幽灵': '#705898',
  '龙': '#7038f8',
  '恶魔': '#705848',
  '机械': '#b8b8d0',
  '光': '#f8d030',
  '神火': '#ff4500',
  '神水': '#1e90ff',
  '神草': '#32cd32'
}
</script>

<style lang="scss" scoped>
.attr-badge {
  display: inline-block;
  padding: 4rpx 16rpx;
  border-radius: 8rpx;
  font-size: 22rpx;
  color: #fff;
  line-height: 1.5;
}
</style>
