<template>
  <div class="boxbackborder box_col">
    <!--<pager-tit title="安全员签到"></pager-tit>-->
    <Menu mode="horizontal" active-name="1" style="margin-bottom: 8px">
      <MenuItem name="1">
        <div style="font-weight: 700;font-size: 16px">
          安全员签到
        </div>
      </MenuItem>
    </Menu>
    <div style="text-align: right">
      <span
        style="cursor: pointer;width: 60px;height: 80px;border:1px solid #30bff5;color:black;padding:6px;border-radius: 4px;margin-left: 16px;"
        @click="param.aqyQdzt='',getData()">总计{{total}}人</span>
      <span
        style="cursor: pointer;width: 60px;height: 80px;border:1px solid #30bff5;color:black;padding:6px;border-radius: 4px;margin-left: 16px;"
        @click="param.aqyQdzt='10',getData()">已签到{{yqdNum}}人</span>
      <span
        style="cursor: pointer;width: 60px;height: 80px;border:1px solid #30bff5;color:black;padding:6px;border-radius: 4px;margin-left: 16px;"
        @click="param.aqyQdzt='00',getData()">未签到{{wqdNum}}人</span>
      <Button style="margin-left: 10px;" type="primary" @click="modalTitle='新增安全员',showModal=true">
        <Icon type="md-add"></Icon>
      </Button>
      <Button type="primary" @click="param.aqyQdzt='',getData()" style="margin-left: 10px">
        <Icon type="md-refresh"/>
        <!--查询-->
      </Button>
    </div>
    <table-area :parent="v" :TabHeight="AF.getPageHeight()-210" :pager="false"></table-area>

    <Modal
      v-model="showModal"
      :JGList="JGList"
      height="400"
      width="900"
      :closable='false'
      :mask-closable="false"
      :title="modalTitle">
      <Form
        ref="form"
        :label-width="100"
        :styles="{top: '20px'}">
        <div style="overflow: auto;width:800px">
          <Row style="display: flex;justify-content: space-between">
            <Col span="10">
              <FormItem label="安全员姓名">
                <Input v-model="AQY.xm" size="large" placeholder="请输入安全员姓名"/>
              </FormItem>
            </Col>
            <Col span="10">
              <FormItem label="安全员联系电话">
                <Input v-model="AQY.sjhm" size="large" placeholder="请输入安全员联系电话"/>
              </FormItem>
            </Col>
          </Row>
          <Row style="display: flex;justify-content: space-between">
            <Col span="10">
              <FormItem label="准驾车型">
                <Select v-model="zjcx" multiple @on-change="changeCx" size="small">
                  <Option v-for="item in cxList" :value="item.val">{{ item.val }}</Option>
                </Select>
              </FormItem>
            </Col>
            <Col span="10">
              <FormItem label="安全员证号">
                <Input v-model="AQY.jlzh" size="large" placeholder="请输入安全员证号"/>
              </FormItem>
            </Col>
          </Row>
          <Row style="display: flex;justify-content: space-between">
            <Col span="10">
              <FormItem label="所属考场">
                <Select v-model="AQY.jgdm">
                  <Option v-for="item in JGList" :value="item.val">{{item.label}}</Option>
                </Select>
              </FormItem>
            </Col>
          </Row>
        </div>
      </Form>
      <div slot='footer'>
        <Button type="default" @click="AQY={},showModal=false" style="color: #949494">取消</Button>
        <Button v-if="modalTitle=='更改安全员信息'" type="error" @click="del">删除</Button>
        <Button type="primary" @click="SaveAQY">确定</Button>
      </div>
    </Modal>

  </div>
</template>

<script>

  export default {
    name: 'char',
    components: {},
    data() {
      return {
        v: this,
        zjcx: [],
        apiRoot: this.apis.zgjbxx,
        choosedItem: null,
        componentName: '',
        pageData: [],
        cxList: [{val: 'C1'}, {val: "C2"}, {val: 'B1'}, {val: 'B2'}, {val: 'A1'}, {val: 'A2'}, {val: 'A3'}],
        tableColumns: [
          {
            title: '序号', align: 'center', minWidth: 90,
            render: (h, p) => {
              return h('Tag', {
                props: {
                  type: 'volcano',
                }
              }, p.row.idx)
            }
          },
          {title: '安全员', align: 'center', key: 'xm', minWidth: 80},
          {title: '安全员电话', align: 'center', key: 'sjhm', minWidth: 80},
          {title: '准驾车型', align: 'center', key: 'zjcx', minWidth: 80},
          {title: '驾驶证号', align: 'center', key: 'jlzh', minWidth: 80},
          {
            title: '签到', fixed: 'right', align: 'center', minWidth: 60,
            render: (h, p) => {
              return h('i-switch', {
                  props: {value: p.row.rzt, disabled: p.row.km == '2'},
                  on: {
                    'on-change': (value) => {
                      p.row.rzt = !p.row.rzt
                      this.change(p.row)
                    }
                  }
                }
              )
            }
          },
          {
            title: '操作', fixed: 'right', align: 'center', width: 60, render: (h, p) => {
              let buttons = [];
              buttons.push(
                h('Tooltip',
                  {props: {placement: 'top', content: '维护信息',}},
                  [
                    h('Button', {
                      props: {
                        type: 'warning',
                        shape: 'circle',
                        size: 'small',
                      },
                      style: {margin: '0 10px 0 0'},
                      on: {
                        click: () => {
                          this.AQY = JSON.parse(JSON.stringify(p.row));
                          this.zjcx = this.AQY.zjcx.split(',')
                          this.modalTitle = '更改安全员信息'
                          this.showModal = true
                        }
                      }
                    }, '改')
                  ]
                ),
              )
              return h('div', buttons);
            }
          },
        ],
        specialPageSize: 9999,
        param: {
          zzzt: '10',
          aqybx: '1',
          gzgw: '0005',
          total: 0,
          xmLike: '',
          pageNum: 1,
          pageSize: 9999,
          notShowLoading: 'true',
          aqyQdzt: '',
        },
        yqdNum: 0,
        wqdNum: 0,
        total: 0,
        showModal: false,
        AQY: {},
        modalTitle: '',
        JGList: []
      }
    },
    created() {
      this.getJgsByOrgCode();
      this.util.initTable(this);
    },
    methods: {
      changeCx(val) {
        this.AQY.zjcx = val.join(',')
        console.log('zjcx', this.AQY.zjcx)
      },
      getJgsByOrgCode() {
        this.$http.get("/api/lccl/getJgsByOrgCode").then(res => {
          if (res.result.length <= 1) {
            this.JGList = []
          }
          for (let r of res.result) {
            let t = {val: r.jgdm, label: r.jgmc}
            this.JGList.push(t)
          }
          this.AQY.jgdm = this.JGList[0].val
        })
        console.log("log", this.JGList);
      },
      getData() {
        this.util.getPageData(this)
      },
      change(item) {
        let rzt = item.rzt ? '10' : '00'
        this.$http.post(this.apis.zgjbxx.setaqrqd, {
          'km': '3',
          'id': item.id,
          'aqyQdzt': rzt,
          notShowLoading: 'true'
        }).then((res) => {
          if (res.code == 200) {
            this.getData()
            this.$Message.success(res.message);
          } else {
            this.$set(this.pageData, (item._index).rzt, false);
            this.$Message.error(res.message);
          }
        })
      },
      afterPager(data) {
        let i = 0;
        if (this.param.aqyQdzt === '') this.yqdNum = this.wqdNum = this.total = 0
        for (let r of data) {
          r.rzt = r.aqyQdzt == '10'
          r.idx = ++i;
          if (this.param.aqyQdzt == '') {
            if (r.rzt === true) {
              this.yqdNum++;
            } else {
              this.wqdNum++;
            }
            this.total++;
          }
        }

        this.pageData = data
      },
      SaveAQY() {
        this.AQY.gzgw = '0005'
        this.AQY.km = '3'
        let url = ''
        if (this.modalTitle === '新增安全员')
          url = '/api/zgjbxx/save'
        if (this.modalTitle === '更改安全员信息')
          url = '/api/zgjbxx/update'

        this.$http.post(url, this.AQY).then((res) => {
          if (res.code == 200) {
            this.$Message.success(res.message);
            this.showModal = false
            this.AQY = {}
            this.getData()
          } else {
            this.$Message.error(res.message);
          }
        })
      },
      del() {
        this.swal({
          title: '确定删除该安全员？',
          type: 'warning',
          showCancelButton: true,
          confirmButtonText: '确定',
          cancelButtonText: '取消',
        }).then((val) => {
          if (val.value) {
            this.$http.post('/api/zgjbxx/remove/' + this.AQY.id).then((res) => {
              if (res.code == 200) {
                this.$Message.success(res.message);
                this.showModal = false
                this.AQY = {}
                this.getData()
              } else {
                this.$Message.error(res.message);
              }
            })
          }
        })
      }
    }
  }
</script>
<style>
  .number {
    text-align: center;
    vertical-align: center;
    font-size: 20px;
    padding-top: 10px;
    width: 40px;
    height: 40px;
    border-radius: 20px;
    /*background-color: red;*/
    color: white;
  }
</style>
