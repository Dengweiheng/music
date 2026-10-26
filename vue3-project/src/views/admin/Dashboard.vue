<template>
  <div class="dashboard-container">
    <div class="header">
      <h2>控制台概览</h2>
      <p>实时监控平台的音乐播放量与用户活跃情况</p>
    </div>

    <!-- 核心指标卡片 -->
    <el-row :gutter="24" class="stat-cards">
      <el-col :span="6" v-for="item in statItems" :key="item.title">
        <div class="stat-card">
          <div class="card-icon" :style="{ background: item.color + '15', color: item.color }">
            <el-icon :size="24"><component :is="item.icon" /></el-icon>
          </div>
          <div class="card-info">
            <span class="title">{{ item.title }}</span>
            <div class="value-row">
              <span class="value">{{ item.value || 0 }}</span>
              <span class="unit">{{ item.unit }}</span>
            </div>
            <p class="trend">
              <span :class="item.increased ? 'up' : 'down'">
                {{ item.increased ? '+' : '-' }}{{ item.trend }}%
              </span>
              较上周
            </p>
          </div>
        </div>
      </el-col>
    </el-row>

    <!-- 图表区域 -->
    <el-row :gutter="24" class="charts-row">
      <el-col :span="12">
        <div class="chart-box">
          <div class="box-header">
            <h3>播放趋势 (Song Plays)</h3>
          </div>
          <div id="playTrendChart" style="height: 300px;"></div>
        </div>
      </el-col>
      <el-col :span="12">
        <div class="chart-box">
          <div class="box-header">
            <h3>用户增长 (New Users)</h3>
          </div>
          <div id="userGrowthChart" style="height: 300px;"></div>
        </div>
      </el-col>
    </el-row>

    <el-row :gutter="24" class="charts-row">
      <el-col :span="8">
        <div class="chart-box">
          <div class="box-header">
            <h3>热门曲风占比</h3>
          </div>
          <div id="categoryChart" style="height: 350px;"></div>
        </div>
      </el-col>
      <el-col :span="16">
        <div class="chart-box ranking-box">
          <div class="box-header">
            <h3>热门歌曲排行榜</h3>
            <el-button link type="primary">详细数据</el-button>
          </div>
          <el-table :data="topMusic" style="width: 100%" size="small">
            <el-table-column type="index" label="排名" width="60" />
            <el-table-column label="曲目">
              <template #default="{ row }">
                <div class="music-cell">
                  <el-image :src="row.music.coverUrl" class="mini-cover" />
                  <span>{{ row.music.title }}</span>
                </div>
              </template>
            </el-table-column>
            <el-table-column prop="music.singer" label="歌手" />
            <el-table-column prop="music.playCount" label="播放量" width="100" />
            <el-table-column label="状态" width="100">
              <template #default>
                <el-tag type="success" size="small" effect="light">飙升</el-tag>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import request from '@/api'
import * as echarts from 'echarts'

const summary = ref({})
const statItems = ref([
  { title: '总播放量', key: 'totalPlays', unit: '次', trend: '12.5', increased: true, icon: 'Headset', color: '#007aff' },
  { title: '新增用户', key: 'newUsers', unit: '人', trend: '8.2', increased: true, icon: 'User', color: '#34c759' },
  { title: '活跃率', key: 'activationRate', unit: '%', trend: '2.1', increased: false, icon: 'Odometer', color: '#ff9500' },
  { title: '在线作品', key: 'onlineWorks', unit: '首', trend: '4.5', increased: true, icon: 'Music', color: '#af52de' }
])

const topMusic = ref([])

const initCharts = () => {
  // 1. Play Trend Chart (Area Line)
  const trendDom = document.getElementById('playTrendChart')
  if (trendDom && summary.value.trend) {
    const trendChart = echarts.init(trendDom)
    trendChart.setOption({
      tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
      grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
      xAxis: { type: 'category', data: summary.value.trend.map(d => d.date) },
      yAxis: { type: 'value' },
      series: [{
        name: '播放量',
        data: summary.value.trend.map(d => d.count),
        type: 'line',
        smooth: true,
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(0, 122, 255, 0.3)' },
            { offset: 1, color: 'rgba(0, 122, 255, 0.05)' }
          ])
        },
        itemStyle: { color: '#007aff' }
      }]
    })
  }

  // 2. User Growth Chart (Bar)
  const growthDom = document.getElementById('userGrowthChart')
  if (growthDom && summary.value.userGrowth) {
    const growthChart = echarts.init(growthDom)
    growthChart.setOption({
      tooltip: { trigger: 'axis' },
      grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
      xAxis: { type: 'category', data: summary.value.userGrowth.map(d => d.date) },
      yAxis: { type: 'value' },
      series: [{
        name: '注冊人数',
        data: summary.value.userGrowth.map(d => d.count),
        type: 'bar',
        barWidth: '40%',
        itemStyle: { 
          color: '#34c759',
          borderRadius: [4, 4, 0, 0]
        }
      }]
    })
  }

  // 3. Category Distribution Chart (Donut Pie)
  const catDom = document.getElementById('categoryChart')
  if (catDom) {
    const catChart = echarts.init(catDom)
    request.get('/stats/categories').then(res => {
      catChart.setOption({
        tooltip: { trigger: 'item' },
        legend: { bottom: '5%', left: 'center', icon: 'circle' },
        series: [{
          name: '曲风占比',
          type: 'pie',
          radius: ['40%', '70%'],
          center: ['50%', '45%'],
          avoidLabelOverlap: false,
          itemStyle: { borderRadius: 10, borderColor: '#fff', borderWidth: 2 },
          label: { show: false },
          data: res || []
        }]
      })
    })
  }
}

onMounted(async () => {
  try {
    const [topRes, summaryRes] = await Promise.all([
      request.get('/stats/top', { params: { limit: 10 } }),
      request.get('/stats/summary')
    ])
    topMusic.value = topRes
    summary.value = summaryRes
    
    statItems.value.forEach(item => {
      item.value = summaryRes[item.key]
    })

    nextTick(() => {
      initCharts()
    })
    
    window.addEventListener('resize', () => {
      ['playTrendChart', 'userGrowthChart', 'categoryChart'].forEach(id => {
        const dom = document.getElementById(id)
        if (dom) echarts.getInstanceByDom(dom)?.resize()
      })
    })
  } catch (e) {
    console.error('Failed to load dashboard:', e)
  }
})
</script>

<style scoped>
.dashboard-container { max-width: 1400px; margin: 0 auto; padding: 0 10px; }
.header { margin-bottom: 32px; }
.header h2 { font-size: 28px; font-weight: 800; color: #1d1d1f; margin: 0 0 8px 0; }
.header p { color: #86868b; margin: 0; }

.stat-cards { margin-bottom: 24px; }
.stat-card { background: white; padding: 24px; border-radius: 20px; box-shadow: 0 4px 20px rgba(0,0,0,0.03); display: flex; gap: 20px; align-items: center; }
.card-icon { width: 56px; height: 56px; border-radius: 16px; display: flex; align-items: center; justify-content: center; }
.card-info { flex: 1; }
.card-info .title { font-size: 13px; color: #86868b; font-weight: 600; }
.value-row { display: flex; align-items: baseline; gap: 4px; margin: 4px 0; }
.value { font-size: 24px; font-weight: 800; color: #1d1d1f; }
.unit { font-size: 14px; color: #515154; }
.trend { font-size: 12px; color: #86868b; margin: 0; }
.trend span.up { color: #34c759; font-weight: 700; }
.trend span.down { color: #ff3b30; font-weight: 700; }

.chart-box { background: white; border-radius: 24px; padding: 24px; box-shadow: 0 4px 20px rgba(0,0,0,0.03); margin-bottom: 24px; transition: transform 0.3s; }
.chart-box:hover { transform: translateY(-4px); }
.box-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 24px; }
.box-header h3 { font-size: 17px; font-weight: 700; margin: 0; color: #333; }

.music-cell { display: flex; align-items: center; gap: 12px; }
.mini-cover { width: 34px; height: 34px; border-radius: 8px; border: 1px solid #eee; }

:deep(.el-table) { --el-table-header-bg-color: #f9f9f9; border-radius: 12px; overflow: hidden; }
:deep(.el-table th) { font-weight: 700; color: #666; }
</style>
