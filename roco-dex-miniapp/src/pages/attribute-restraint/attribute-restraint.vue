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

    <!-- 克制表 -->
    <scroll-view scroll-x scroll-y class="table-wrap">
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
const attributes = [
  '普通', '火', '水', '草', '电', '冰', '翼', '土',
  '萌', '虫', '幽灵', '龙', '恶魔', '机械', '光'
]

// 克制关系数据: multiplierMap[攻击][防御] = 倍率
const multiplierMap: Record<string, Record<string, number>> = {
  '火': { '草': 2, '冰': 2, '虫': 2, '机械': 2, '水': 0.5, '火': 0.5, '土': 0.5, '龙': 0.5 },
  '水': { '火': 2, '土': 2, '水': 0.5, '草': 0.5, '龙': 0.5 },
  '草': { '水': 2, '土': 2, '火': 0.5, '草': 0.5, '翼': 0.5, '虫': 0.5, '龙': 0.5, '机械': 0.5 },
  '电': { '水': 2, '翼': 2, '草': 0.5, '电': 0.5, '土': 0, '龙': 0.5 },
  '冰': { '草': 2, '土': 2, '翼': 2, '龙': 2, '火': 0.5, '水': 0.5, '冰': 0.5, '机械': 0.5 },
  '土': { '火': 2, '电': 2, '机械': 2, '水': 0.5, '草': 0.5, '翼': 0 },
  '翼': { '草': 2, '虫': 2, '电': 0.5, '冰': 0.5, '土': 0 },
  '萌': { '萌': 0.5, '机械': 0.5, '幽灵': 0 },
  '虫': { '草': 2, '萌': 2, '恶魔': 2, '火': 0.5, '翼': 0.5, '机械': 0.5 },
  '幽灵': { '萌': 2, '幽灵': 2, '普通': 0, '恶魔': 0.5 },
  '龙': { '龙': 2, '机械': 0.5 },
  '恶魔': { '萌': 2, '幽灵': 2, '恶魔': 0.5, '机械': 0.5 },
  '机械': { '冰': 2, '萌': 0.5, '火': 0.5, '水': 0.5, '电': 0.5, '机械': 0.5 },
  '光': { '幽灵': 2, '恶魔': 2, '光': 0.5 },
  '普通': { '幽灵': 0 }
}

function getMultiplier(atk: string, def: string): string {
  if (atk === def) return '1'
  const val = multiplierMap[atk]?.[def]
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
</style>
