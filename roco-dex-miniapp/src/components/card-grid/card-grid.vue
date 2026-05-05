<template>
  <view class="card-grid">
    <view
      v-for="item in items"
      :key="item.id"
      class="grid-item"
      @tap="onTap(item)"
    >
      <image
        class="item-image"
        :src="item.imageUrl || '/static/images/default.png'"
        mode="aspectFit"
      />
      <text class="item-name">{{ item.name }}</text>
      <slot name="extra" :item="item" />
    </view>
    <view v-if="items.length === 0" class="empty">
      <text>暂无数据</text>
    </view>
  </view>
</template>

<script setup lang="ts">
defineProps<{
  items: Array<{ id: number; name: string; imageUrl?: string }>
}>()

const emit = defineEmits<{
  (e: 'tap', item: any): void
}>()

function onTap(item: any) {
  emit('tap', item)
}
</script>

<style lang="scss" scoped>
.card-grid {
  display: flex;
  flex-wrap: wrap;
  padding: 16rpx;
}

.grid-item {
  width: 25%;
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 16rpx 0;
  box-sizing: border-box;
}

.item-image {
  width: 100rpx;
  height: 100rpx;
  border-radius: 16rpx;
  background: #f5f5f5;
}

.item-name {
  font-size: 24rpx;
  color: #333;
  margin-top: 8rpx;
  text-align: center;
  max-width: 140rpx;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.empty {
  width: 100%;
  text-align: center;
  padding: 60rpx 0;
  color: #999;
}
</style>
