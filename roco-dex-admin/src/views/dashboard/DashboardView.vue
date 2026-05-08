<template>
  <div class="dashboard">
    <el-row :gutter="20" class="stat-row">
      <el-col :span="4" v-for="item in statCards" :key="item.label">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-info">
              <div class="stat-label">{{ item.label }}</div>
              <div class="stat-value">{{ item.value }}</div>
            </div>
            <el-icon :size="40" :color="item.color">
              <component :is="item.icon" />
            </el-icon>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="chart-row">
      <el-col :span="14">
        <el-card shadow="hover">
          <template #header>
            <span>近7天用户注册趋势</span>
          </template>
          <div ref="trendChartRef" class="chart-container"></div>
        </el-card>
      </el-col>
      <el-col :span="10">
        <el-card shadow="hover">
          <template #header>
            <span>宠物属性分布</span>
          </template>
          <div ref="pieChartRef" class="chart-container"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, onBeforeUnmount, nextTick } from 'vue'
import request from '@/utils/request'
import * as echarts from 'echarts'

const trendChartRef = ref<HTMLElement>()
const pieChartRef = ref<HTMLElement>()
let trendChart: echarts.ECharts | null = null
let pieChart: echarts.ECharts | null = null

const statCards = reactive([
  { label: '宠物数量', value: 0, icon: 'ElementPlus', color: '#409eff' },
  { label: '技能数量', value: 0, icon: 'MagicStick', color: '#67c23a' },
  { label: '道具数量', value: 0, icon: 'Box', color: '#e6a23c' },
  { label: '装备数量', value: 0, icon: 'Suitcase', color: '#f56c6c' },
  { label: '用户数量', value: 0, icon: 'User', color: '#909399' }
])

async function loadStats() {
  try {
    const res: any = await request.get('/dashboard/stats')
    const data = res.data
    statCards[0].value = data.petCount || 0
    statCards[1].value = data.skillCount || 0
    statCards[2].value = data.itemCount || 0
    statCards[3].value = data.equipmentCount || 0
    statCards[4].value = data.userCount || 0
  } catch (error) {
    // Use default values
  }
}

async function loadTrendChart() {
  try {
    const res: any = await request.get('/dashboard/trend')
    const data = res.data || []

    await nextTick()
    if (!trendChartRef.value) return

    trendChart = echarts.init(trendChartRef.value)
    trendChart.setOption({
      tooltip: { trigger: 'axis' },
      grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
      xAxis: {
        type: 'category',
        boundaryGap: false,
        data: data.map((item: any) => item.date)
      },
      yAxis: { type: 'value' },
      series: [
        {
          name: '注册人数',
          type: 'line',
          smooth: true,
          areaStyle: { opacity: 0.3 },
          itemStyle: { color: '#409eff' },
          data: data.map((item: any) => item.count)
        }
      ]
    })
  } catch (error) {
    // Use empty chart
    await nextTick()
    if (trendChartRef.value) {
      trendChart = echarts.init(trendChartRef.value)
      trendChart.setOption({
        title: { text: '暂无数据', left: 'center', top: 'center' }
      })
    }
  }
}

async function loadPieChart() {
  try {
    const res: any = await request.get('/dashboard/attr-dist')
    const data = res.data || []

    await nextTick()
    if (!pieChartRef.value) return

    pieChart = echarts.init(pieChartRef.value)
    pieChart.setOption({
      tooltip: { trigger: 'item', formatter: '{b}: {c} ({d}%)' },
      legend: { orient: 'vertical', left: 'left' },
      series: [
        {
          name: '属性分布',
          type: 'pie',
          radius: '55%',
          center: ['50%', '60%'],
          data: data.map((item: any) => ({
            name: item.attribute,
            value: item.count
          })),
          emphasis: {
            itemStyle: {
              shadowBlur: 10,
              shadowOffsetX: 0,
              shadowColor: 'rgba(0, 0, 0, 0.5)'
            }
          }
        }
      ]
    })
  } catch (error) {
    await nextTick()
    if (pieChartRef.value) {
      pieChart = echarts.init(pieChartRef.value)
      pieChart.setOption({
        title: { text: '暂无数据', left: 'center', top: 'center' }
      })
    }
  }
}

function handleResize() {
  trendChart?.resize()
  pieChart?.resize()
}

onMounted(() => {
  loadStats()
  loadTrendChart()
  loadPieChart()
  window.addEventListener('resize', handleResize)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize)
  trendChart?.dispose()
  pieChart?.dispose()
})
</script>

<style scoped lang="scss">
.dashboard {
  .stat-row {
    margin-bottom: 20px;

    .stat-card {
      .stat-content {
        display: flex;
        align-items: center;
        justify-content: space-between;

        .stat-label {
          font-size: 14px;
          color: #909399;
          margin-bottom: 8px;
        }

        .stat-value {
          font-size: 28px;
          font-weight: 600;
          color: #303133;
        }
      }
    }
  }

  .chart-row {
    .chart-container {
      height: 350px;
    }
  }
}
</style>
