<template>
  <scroll-view class="filter-tabs" scroll-x enable-flex>
    <view
      v-for="tab in tabs"
      :key="tab.value"
      class="tab-item"
      :class="{ active: modelValue === tab.value }"
      @tap="onSelect(tab.value)"
    >
      <text>{{ tab.label }}</text>
    </view>
  </scroll-view>
</template>

<script setup lang="ts">
defineProps<{
  modelValue: string
  tabs: Array<{ label: string; value: string }>
}>()

const emit = defineEmits<{
  (e: 'update:modelValue', value: string): void
  (e: 'change', value: string): void
}>()

function onSelect(value: string) {
  emit('update:modelValue', value)
  emit('change', value)
}
</script>

<style lang="scss" scoped>
.filter-tabs {
  white-space: nowrap;
  padding: 16rpx 24rpx;
  background: #fff;
}

.tab-item {
  display: inline-block;
  padding: 12rpx 28rpx;
  margin-right: 16rpx;
  border-radius: 32rpx;
  font-size: 26rpx;
  color: #666;
  background: #f5f5f5;

  &.active {
    background: #4a90d9;
    color: #fff;
  }
}
</style>
