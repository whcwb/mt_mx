import $http from '@/axios/index'
import swal from 'sweetalert2';

var obj = {}

obj.print = (id,item,time,callback) => {
  var printJson = [
    {'x': 10, 'y': 30, 'w': 250, 'h': 30, 'isbar': 'F', 'border': 1, 'text': '车辆租赁凭据', 'font_size': 12},
    {'x': 10, 'y': 60, 'w': 250, 'h': 80, 'isbar': 'T', 'border': 1, 'text': id, 'font_size': 15},
    {'x': 10, 'y': 140, 'w': 100, 'h': 30, 'isbar': 'F', 'border': 1, 'text': '训练科目', 'font_size': 12},
    {'x': 110, 'y': 140, 'w': 150, 'h': 30, 'isbar': 'F', 'border': 1, 'text': '科目二', 'font_size': 12},
    {'x': 10, 'y': 170, 'w': 100, 'h': 30, 'isbar': 'F', 'border': 1, 'text': '车辆编号', 'font_size': 12},
    {'x': 110, 'y': 170, 'w': 150, 'h': 30, 'isbar': 'F', 'border': 1, 'text': '', 'font_size': 12},
    {'x': 10, 'y': 200, 'w': 100, 'h': 30, 'isbar': 'F', 'border': 1, 'text': '训练车型', 'font_size': 12},
    {'x': 110, 'y': 200, 'w': 150, 'h': 30, 'isbar': 'F', 'border': 1, 'text': '', 'font_size': 12},
    {'x': 10, 'y': 230, 'w': 100, 'h': 30, 'isbar': 'F', 'border': 1, 'text': '教练员', 'font_size': 12},
    {'x': 110, 'y': 230, 'w': 150, 'h': 30, 'isbar': 'F', 'border': 1, 'text': '张平', 'font_size': 12},
    {'x': 10, 'y': 260, 'w': 100, 'h': 30, 'isbar': 'F', 'border': 1, 'text': '安全员', 'font_size': 12},
    {'x': 110, 'y': 260, 'w': 150, 'h': 30, 'isbar': 'F', 'border': 1, 'text': '', 'font_size': 12},
    {'x': 10, 'y': 290, 'w': 100, 'h': 30, 'isbar': 'F', 'border': 1, 'text': '累计时长', 'font_size': 12},
    {'x': 110, 'y': 290, 'w': 150, 'h': 30, 'isbar': 'F', 'border': 1, 'text': '', 'font_size': 12},
    {'x': 10, 'y': 320, 'w': 100, 'h': 30, 'isbar': 'F', 'border': 1, 'text': '累计费用', 'font_size': 12},
    {'x': 110, 'y': 320, 'w': 150, 'h': 30, 'isbar': 'F', 'border': 1, 'text': '300元', 'font_size': 12},
    {'x': 10, 'y': 350, 'w': 100, 'h': 30, 'isbar': 'F', 'border': 1, 'text': '备注', 'font_size': 12},
    {'x': 110, 'y': 350, 'w': 150, 'h': 30, 'isbar': 'F', 'border': 1, 'text': '1人', 'font_size': 9},
    {'x': 10, 'y': 380, 'w': 100, 'h': 30, 'isbar': 'F', 'border': 0, 'text': '本票据遗失不补', 'font_size': 9},
    {'x': 125, 'y': 380, 'w': 150, 'h': 30, 'isbar': 'F', 'border': 0, 'text': time, 'font_size': 9}
  ];

  let itemIndex=0;
  printJson.map((val,index,arr)=>{
    if(index>2&&index%2!==0&&index<18){
      val.text=item[itemIndex]
      itemIndex++;
    }
  })

  $.ajax({
    type: "POST",
    url: "http://127.0.0.1:39999/print",
    data: JSON.stringify(printJson),
    contentType: "application/json",
    dataType: "json",
    beforeSend: function () { },
    success: function (data) {
      // alert("打印成功="+data);
    },
    error:function(data){
      // alert("服务未启动，请先启动监听服务");
    }
  });

}

export default obj

