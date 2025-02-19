<template>
    <!-- 统计卡片 -->
    <div class="stats-cards">
        <el-card v-for="(card, index) in statsCards" :key="index" shadow="hover">
            <div class="card-content">
                <div class="card-icon" :style="{ backgroundColor: card.color }">
                    <component :is="card.icon" />
                </div>
                <div class="card-info">
                    <div class="card-title">{{ card.title }}</div>
                    <div class="card-value">{{ card.value }}</div>
                </div>
            </div>
        </el-card>
    </div>

    <!-- 图表区域 -->
    <el-row :gutter="20" class="chart-area">
        <el-col :span="16">
            <el-card shadow="hover">
                <template #header>
                    <span>用户增长趋势</span>
                </template>
                <div ref="lineChartDom" class="chart-container"></div>
            </el-card>
        </el-col>

        <el-col :span="8">
            <el-card shadow="hover">
                <template #header>
                    <span>产品分布</span>
                </template>
                <div ref="pieChartDom" class="chart-container"></div>
            </el-card>
        </el-col>
    </el-row>
</template>

<script setup>
import * as echarts from 'echarts'
import { ref, onMounted, onBeforeUnmount } from 'vue'
import {
    User,
    Goods,
    Money,
    TrendCharts
} from '@element-plus/icons-vue'

// 统计卡片数据
const statsCards = ref([
    {
        title: '总用户数',
        value: '2,845',
        icon: User,
        color: '#409EFF'
    },
    {
        title: '今日订单',
        value: '¥ 45,230',
        icon: Goods,
        color: '#67C23A'
    },
    {
        title: '销售额',
        value: '1,234',
        icon: Money,
        color: '#E6A23C'
    },
    {
        title: '转化率',
        value: '78%',
        icon: TrendCharts,
        color: '#F56C6C'
    }
])

// 图表实例
let lineChart = null
let pieChart = null
const lineChartDom = ref(null)
const pieChartDom = ref(null)

// 初始化图表
const initCharts = () => {
    // 折线图配置
    lineChart = echarts.init(lineChartDom.value)
    lineChart.setOption({
        xAxis: {
            type: 'category',
            data: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun']
        },
        yAxis: {
            type: 'value'
        },
        series: [{
            data: [820, 932, 901, 934, 1290, 1330, 1320],
            type: 'line',
            smooth: true
        }]
    })

    // 饼图配置
    pieChart = echarts.init(pieChartDom.value)
    pieChart.setOption({
        tooltip: {
            trigger: 'item'
        },
        series: [{
            type: 'pie',
            radius: '50%',
            data: [
                { value: 1048, name: '产品A' },
                { value: 735, name: '产品B' },
                { value: 580, name: '产品C' },
                { value: 484, name: '产品D' }
            ],
            emphasis: {
                itemStyle: {
                    shadowBlur: 10,
                    shadowOffsetX: 0,
                    shadowColor: 'rgba(0, 0, 0, 0.5)'
                }
            }
        }]
    })
}

// 窗口大小改变时重置图表
const handleResize = () => {
    lineChart?.resize()
    pieChart?.resize()
}
onMounted(() => {
    initCharts()
    window.addEventListener('resize', handleResize)
})

onBeforeUnmount(() => {
    window.removeEventListener('resize', handleResize)
    lineChart?.dispose()
    pieChart?.dispose()
})
</script>

<style scoped>
.stats-cards {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
    gap: 20px;
    margin-bottom: 20px;
}

.card-content {
    display: flex;
    align-items: center;
    padding: 20px;
}

.card-icon {
    width: 50px;
    height: 50px;
    border-radius: 8px;
    display: flex;
    align-items: center;
    justify-content: center;
    margin-right: 15px;
}

.card-icon svg {
    width: 24px;
    height: 24px;
    color: white;
}

.card-title {
    color: #909399;
    font-size: 14px;
    margin-bottom: 8px;
}

.card-value {
    font-size: 24px;
    font-weight: bold;
}

.chart-area {
    margin-top: 20px;
}

.chart-container {
    height: 400px;
}
</style>