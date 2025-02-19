<template>
    <div>
        <div ref="qrcodeCanvas" :width="width" :height="height"></div>
    </div>
</template>

<script>
import QRCode from 'qrcodejs2-fix'
import { onMounted, ref, watch } from 'vue';

export default {
    name: 'QRCode',
    props: {
        value: {
            type: String,
            required: true,
            default: ''
        },
        width: {
            type: Number,
            default: 200
        },
        height: {
            type: Number,
            default: 200
        },
        // 其他可能的props，如颜色、容错级别等
    },
    setup(props) {
        const qrcodeCanvas = ref();
        const createQrCode = () => {
            let qrcode = new QRCode(qrcodeCanvas.value, {
                text: props.value,
                width: props.width,
                height: props.height,
                colorDark: '#3990e1', // 二维码颜色
                colorLight: '#ffffff', //背景颜色
                correctLevel: QRCode.CorrectLevel.H
            })
        }
        onMounted(() => {
            createQrCode()
        });

        return {
            qrcodeCanvas
        };
    }
};
</script>

<style scoped>
/* 可选的样式 */
canvas {
    border: 1px solid #fffefe;
}
</style>