<template>
    <div class="main-class">
        <div class="login-container">
            <div class="login-methods">
                <div :class="['method-item', activeTab === 'verify' && 'active']" @click="activeTab = 'verify'">
                    验证码登录
                </div>
                <div :class="['method-item', activeTab === 'password' && 'active']" @click="activeTab = 'password'">
                    密码登录
                </div>
                <div :class="['method-item', activeTab === 'qrcode' && 'active']" @click="activeTab = 'qrcode'">
                    二维码登录
                </div>
            </div>

            <div class="form-container">
                <div v-if="activeTab === 'verify'" class="phone-input">
                    <el-select v-model="areaCode" class="area-code">
                        <el-option label="中国 +86" value="+86" />
                    </el-select>
                    <el-input v-model="phone" placeholder="请输入手机号" />
                </div>

                <div v-if="activeTab === 'verify'" class="verify-code">
                    <el-input v-model="code" placeholder="输入6位短信验证码" />
                    <el-button type="primary" :disabled="countdown > 0" @click="getVerifyCode">
                        {{ countdownText }}
                    </el-button>
                </div>

                <div v-if="activeTab === 'password'" class="phone-input">
                    <span style="width: 75px;">用户名:</span>
                    <el-input v-model="phone" placeholder="请输入用户名" type="text" />
                </div>

                <div v-if="activeTab === 'password'" class="phone-input">
                    <!-- <el-select v-model="areaCode" class="area-code">
                    <el-option label="中国 +86" value="+86" />
                </el-select> -->
                <span style="width: 75px;">密码:</span>
                    <el-input v-model="phone" placeholder="请输入密码" type="password" />
                </div>
                <!-- <div class="voice-code">
          <el-link type="info" @click="getVoiceCode">获取语音验证码</el-link>
        </div> -->

                <el-button v-if="activeTab != 'qrcode'" type="primary" class="login-btn" @click="handleLogin">
                    登录/注册
                </el-button>

                <div style="padding-left: 30%;">
                    <qrcode v-if="activeTab === 'qrcode'" value="www.baidu.com" :width="150" :height="150"></qrcode>
                </div>
            </div>

            <div class="divider">其他扫码方式：微信</div>

            <div class="footer-links">
                <el-link type="info">下载App</el-link>
                <!-- <el-link type="info">开通机构号</el-link> -->
                <!-- <el-link type="info">无障碍模式</el-link> -->
            </div>

            <div class="agreement">
                未注册手机验证后自动登录
                <!-- ，注册即代表同意
                <el-link type="primary">《知乎协议》</el-link>
                <el-link type="primary">《隐私保护指引》</el-link> -->
            </div>
        </div>
    </div>
</template>

<script setup>
// import { ElMessage } from 'element-plus'
import { ref, computed } from 'vue'
import qrcode from '../../components/qrcode.vue'

const activeTab = ref('password')
const areaCode = ref('+86')
const phone = ref('')
const code = ref('')
const countdown = ref(0)

const countdownText = computed(() =>
    countdown.value > 0 ? `${countdown.value}秒后重发` : '获取短信验证码'
)

const getVerifyCode = () => {
    if (!phone.value) {
        ElMessage.error('请输入手机号')
        return
    }
    countdown.value = 60
    const timer = setInterval(() => {
        countdown.value--
        if (countdown.value <= 0) clearInterval(timer)
    }, 1000)
}

const handleLogin = () => {
    // 处理登录逻辑
}
</script>

<style scoped>
.main-class {
    height: 98vh;
    width: 100%;
    background-color: #b8e5f8;
    background-image: url(https://static.zhihu.com/heifetz/assets/sign_bg.47eec442.png);
    display: flex;
    justify-content: center;
    align-items: center;
}

.login-container {
    height: 400px;
    width: 400px;
    padding: 20px;
    background: #fff;

}

.login-methods {
    display: flex;
    margin-bottom: 30px;
}

.method-item {
    flex: 1;
    text-align: center;
    padding: 15px;
    cursor: pointer;
    color: #999;
    border-bottom: 2px solid transparent;
}

.method-item.active {
    color: #0084ff;
    border-bottom-color: #0084ff;
    font-weight: bold;
}

.phone-input {
    display: flex;
    gap: 10px;
    margin-bottom: 20px;
}

.area-code {
    width: 120px;
}

.verify-code {
    display: flex;
    gap: 10px;
    margin-bottom: 10px;
}

.login-btn {
    width: 100%;
    margin-top: 20px;
}

.divider {
    margin: 30px 0;
    text-align: center;
    color: #999;
    position: relative;
}

.divider::before,
.divider::after {
    content: '';
    position: absolute;
    top: 50%;
    width: 30%;
    height: 1px;
    background: #eee;
}

.divider::before {
    left: 0;
}

.divider::after {
    right: 0;
}

.footer-links {
    display: flex;
    justify-content: center;
    gap: 20px;
    margin: 20px 0;
}

.agreement {
    text-align: center;
    color: #999;
    font-size: 12px;
}

.voice-code {
    text-align: right;
    margin-bottom: 20px;
}
</style>