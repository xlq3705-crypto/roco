<template>
  <view class="page-restraint">
    <!-- 说明 -->
    <view class="legend">
      <view class="legend-item">
        <view class="legend-dot super">2</view>
        <text>克制</text>
      </view>
      <view class="legend-item">
        <view class="legend-dot normal">1</view>
        <text>普通</text>
      </view>
      <view class="legend-item">
        <view class="legend-dot weak">½</view>
        <text>抵抗</text>
      </view>
      <view class="legend-item">
        <view class="legend-dot immune">0</view>
        <text>免疫</text>
      </view>
    </view>

    <!-- 提示 -->
    <view class="tip">
      <text>行=攻击方属性，列=防御方属性</text>
    </view>

    <!-- 加载中 -->
    <view v-if="loading" class="loading-wrap">
      <text>加载中...</text>
    </view>

    <!-- 加载失败 -->
    <view v-else-if="loadError" class="loading-wrap">
      <text>数据加载失败，请检查后端服务是否启动</text>
      <view class="retry-btn" @tap="fetchAttributeTable">重试</view>
    </view>

    <!-- 克制表 -->
    <scroll-view v-else scroll-x scroll-y class="table-wrap">
      <view class="table">
        <!-- 表头 -->
        <view class="table-row header-row">
          <view class="table-cell corner">攻\防</view>
          <view
            v-for="attr in attributes"
            :key="attr"
            class="table-cell header-cell"
          >
            <text>{{ attr }}</text>
          </view>
        </view>

        <!-- 数据行 -->
        <view
          v-for="atk in attributes"
          :key="atk"
          class="table-row"
        >
          <view class="table-cell row-header">
            <text>{{ atk }}</text>
          </view>
          <view
            v-for="def in attributes"
            :key="def"
            class="table-cell"
            :class="getCellClass(atk, def)"
          >
            <text>{{ getMultiplier(atk, def) }}</text>
          </view>
        </view>
      </view>
    </scroll-view>
  </view>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { get } from '@/utils/request'
import { onLoad } from '@dcloudio/uni-app'

interface AttributeRow {
  attackAttr: string
  defenseAttr: string
  multiplier: number
}

const attributes = ref<string[]>([])
const multiplierMap = ref<Record<string, Record<string, number>>>({})
const loading = ref(true)
const loadError = ref(false)

onLoad(() => {
  fetchAttributeTable()
})

async function fetchAttributeTable() {
  loading.value = true
  loadError.value = false
  try {
    const res = await get<AttributeRow[]>('/api/attribute/table')
    const rows = res.data
    const attrSet = new Set<string>()
    const map: Record<string, Record<string, number>> = {}

    for (const row of rows) {
      attrSet.add(row.attackAttr)
      attrSet.add(row.defenseAttr)
      if (!map[row.attackAttr]) map[row.attackAttr] = {}
      map[row.attackAttr][row.defenseAttr] = Number(row.multiplier)
    }

    attributes.value = Array.from(attrSet)
    multiplierMap.value = map
  } catch (e) {
    loadError.value = true
    uni.showToast({ title: '加载克制数据失败', icon: 'none' })
  } finally {
    loading.value = false
  }
}

function getMultiplier(atk: string, def: string): string {
  if (atk === def) return '1'
  const val = multiplierMap.value[atk]?.[def]
  if (val === undefined) return '1'
  if (val === 2) return '2'
  if (val === 0.5) return '½'
  if (val === 0) return '0'
  return '1'
}

function getCellClass(atk: string, def: string): string {
  const val = getMultiplier(atk, def)
  if (val === '2') return 'super'
  if (val === '½') return 'weak'
  if (val === '0') return 'immune'
  return ''
}
</script>

<style lang="scss" scoped>
.page-restraint {
  min-height: 100vh;
  background: #f5f5f5;
  padding: 20rpx;
}

.legend {
  display: flex;
  justify-content: center;
  gap: 30rpx;
  margin-bottom: 16rpx;
  background: #fff;
  border-radius: 12rpx;
  padding: 16rpx;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 8rpx;
  font-size: 22rpx;
  color: #666;
}

.legend-dot {
  width: 40rpx;
  height: 40rpx;
  border-radius: 8rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20rpx;
  font-weight: bold;
  color: #fff;

  &.super { background: #e74c3c; }
  &.normal { background: #95a5a6; }
  &.weak { background: #3498db; }
  &.immune { background: #2c3e50; }
}

.tip {
  text-align: center;
  font-size: 22rpx;
  color: #999;
  margin-bottom: 16rpx;
}

.table-wrap {
  background: #fff;
  border-radius: 12rpx;
  overflow: hidden;
}

.table {
  display: inline-block;
  min-width: 100%;
}

.table-row {
  display: flex;
}

.table-cell {
  width: 80rpx;
  min-width: 80rpx;
  height: 60rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20rpx;
  border-bottom: 1rpx solid #f0f0f0;
  border-right: 1rpx solid #f0f0f0;

  &.super {
    background: #fde8e8;
    color: #e74c3c;
    font-weight: bold;
  }

  &.weak {
    background: #e8f4fd;
    color: #3498db;
  }

  &.immune {
    background: #eaecee;
    color: #2c3e50;
    font-weight: bold;
  }
}

.corner {
  background: #f8f9fa;
  font-weight: bold;
  color: #333;
  font-size: 18rpx;
}

.header-cell {
  background: #f8f9fa;
  font-weight: bold;
  color: #333;
  font-size: 18rpx;
}

.row-header {
  background: #f8f9fa;
  font-weight: bold;
  color: #333;
  font-size: 18rpx;
}

.header-row .table-cell {
  border-bottom: 2rpx solid #ddd;
}

.loading-wrap {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 100rpx 0;
  color: #999;
  font-size: 28rpx;
}

.retry-btn {
  margin-top: 24rpx;
  padding: 12rpx 40rpx;
  background: #4a90d9;
  color: #fff;
  border-radius: 8rpx;
  font-size: 26rpx;
}
</style>
