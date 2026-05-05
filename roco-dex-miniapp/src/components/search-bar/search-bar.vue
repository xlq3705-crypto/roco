<template>
  <view class="search-bar">
    <view class="search-input-wrap">
      <text class="search-icon">&#x1F50D;</text>
      <input
        class="search-input"
        type="text"
        :placeholder="placeholder"
        :value="modelValue"
        @input="onInput"
        @confirm="onSearch"
        confirm-type="search"
      />
      <text v-if="modelValue" class="clear-btn" @tap="onClear">&#x2715;</text>
    </view>
  </view>
</template>

<script setup lang="ts">
defineProps<{
  modelValue?: string
  placeholder?: string
}>()

const emit = defineEmits<{
  (e: 'update:modelValue', value: string): void
  (e: 'search', value: string): void
}>()

function onInput(e: any) {
  emit('update:modelValue', e.detail.value)
}

function onSearch() {
  emit('search', '')
}

function onClear() {
  emit('update:modelValue', '')
  emit('search', '')
}
</script>

<style lang="scss" scoped>
.search-bar {
  padding: 16rpx 24rpx;
  background: #fff;
}

.search-input-wrap {
  display: flex;
  align-items: center;
  background: #f5f5f5;
  border-radius: 36rpx;
  padding: 0 24rpx;
  height: 72rpx;
}

.search-icon {
  font-size: 28rpx;
  margin-right: 12rpx;
}

.search-input {
  flex: 1;
  font-size: 28rpx;
  height: 72rpx;
}

.clear-btn {
  font-size: 28rpx;
  color: #999;
  padding: 0 8rpx;
}
</style>
