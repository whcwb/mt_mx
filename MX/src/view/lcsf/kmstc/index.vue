<template>
  <div class="box_col" style="margin-left: 20px;">
    <Menu mode="horizontal" active-name="1">
      <MenuItem name="1">
        <div style="font-weight: 700;font-size: 16px">
          套餐维护
        </div>
      </MenuItem>
    </Menu>
    <div class="box_col_auto" :style="{height:AF.getPageHeight()-150+'px',overflowX:'hidden',flex:'unset'}">
      <Row :gutter="12">
        <Col span="8" v-for="(item,index) in list" :prop="item.zdmc" :key="item.zdId">
          <Card style="margin-top: 12px;">
            <p slot="title">
              <Icon type="ios-car"></Icon>
              {{item.by9}}【{{item.by8}}】
            </p>
            <Row>
              <Col span="20" v-if="item.zddm =='K3KF'">
                <InputNumber v-model="item.zdmc" :placeholder="'请填写练车价格'" style="width: 200px;"
                             @on-change="change(item)"></InputNumber>
                <span v-if="item.zddm =='K3PY'"> 元套餐</span>
                <span v-else-if="item.zddm =='K3KF'">元/人</span>
                <span v-else> 元套餐</span>
              </Col>
              <Col span="20" v-if="item.zddm !='K3KF'&&item.zddm !='K3PY'">
                <InputNumber v-model="item.zdmc" :placeholder="'请填写练车单价...'" style="width: 200px;"></InputNumber>
                <span v-if="item.by5=='00'">元/小时</span>
                <span v-else-if="item.by5=='10'">元/人</span>
                <span v-else> 元/人</span>
              </Col>
              <Col span="20" v-if="item.zddm =='K3PY'">
                <InputNumber v-model="item.zdmc" :placeholder="'请填写练车单价...'" style="width: 200px;"></InputNumber>
                <span>元/人</span>
              </Col>
            </Row>
            <Row style="margin-top: 16px;">
              <Col span="20">
                <InputNumber v-model="item.by3" :placeholder="'请填写练车单价...'" style="width: 200px;"></InputNumber>
                <span v-if="item.zddm =='K3PY'"> 元/人</span>
                <span v-else-if="item.zddm =='K3KF'">返点金额</span>
                <!--<span v-else> 元/分钟</span>-->
                <span v-if="item.by5=='00'">元/分钟</span>
                <span v-else-if="item.by5=='10'">返点金额</span>
                <span v-else> 元/人</span>
              </Col>
              <Col span="4">
              </Col>
            </Row>
            <Row style="margin-top: 16px;">
              <Col span="20">
                <InputNumber v-model="item.by4" :placeholder="'请填写练车单价...'" style="width: 200px;"></InputNumber>
                <span>返点率</span>
              </Col>
              <Col span="4">
                <Button type="primary" @click="confirm(item)">修改</Button>
              </Col>
            </Row>
            <Row style="margin-top: 16px;">
              <Col span="24">
                <i-switch v-model="item.by2" @on-change="confirm(item)"></i-switch>
                <span>启用【刷卡发车】</span>
              </Col>
            </Row>
            <Row style="margin-top: 16px;">
              <Col span="24">
                <i-switch v-model="item.by6" @on-change="confirm(item)"></i-switch>
                <span>启用【车辆绑卡】</span>
              </Col>
            </Row>
            <Row style="margin-top: 16px;">
              <Col span="24">
                <i-switch v-model="item.by7" @on-change="confirm(item)"></i-switch>
                <span>启用【刷卡点火】</span>
              </Col>
            </Row>
          </Card>
        </Col>
      </Row>
    </div>
  </div>
</template>

<script>
  export default {
    name: "index",
    data() {
      return {
        v: this,
        formItem: {},
        ruleInline: {},
        formItemGroup: [],
        list: []
      }
    },
    methods: {
      change(item) {
        item.by3 = parseInt(item.zdmc / 60);
      },
      confirm(item) {
        item.by2 = item.by2 ? '1' : '0'
        item.by6 = item.by6 ? '1' : '0'
        item.by7 = item.by7 ? '1' : '0'
        this.$http.post(this.apis.DICTIONARY_LIST.CHANGE, item).then((res) => {
          if (res.code == 200) {
            this.$Message.success(res.message);
            this.getData()
          } else {
            this.$Messgae.error(res.message);
          }
        })
      },
      getData() {
        let param = {
          zdlmdm: 'ZDCLK1045',
          by1: '科三'
        }
        this.$http.get(this.apis.DICTIONARY_LIST.list, {params: param}).then((res) => {
          if (res.code == 200 && res.result) {
            this.list = res.result
            for (let r of this.list) {
              r.editMode = false
              r.zdmc = parseInt(r.zdmc)
              r.by3 = parseFloat(r.by3)
              r.by4 = parseFloat(r.by4)
              r.by2 = r.by2 === '0' || r.by2 === '' ? false : true
              r.by6 = r.by6 === '0' || r.by6 === '' ? false : true
              r.by7 = r.by7 === '0' || r.by7 === '' ? false : true
            }
          } else {
            this.$Message.error(res.message)
          }
        })
      }
    },
    mounted() {

    },
    created() {
      this.getData();
    }
  }
</script>

<style scoped>
</style>
