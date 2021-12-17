const $body = $('body');
const $eye = $('.eye');
const $pupil = $('.pupil');
const $switchWrapper = $('.switch-wrapper');
const $animalBody = $('#animal-body');
const $explainGood = $('#explainGood');


var JavaApp = 'test';

function login() {
  JavaApp.login();
}

function loginProcess (){
  JavaApp.loginProcess();
}

function loginFile () {
  JavaApp.loginFile();
}

$(function(){

  $(".imglist li").click(function(){
    // 获取当前图片的src
    var src=$(this).find(".imgbox img").attr('src');
    //添加到弹窗img
    $(".bigimg").attr("src",src);
    // 显示弹窗
    $(".maskweap").fadeIn();
  });
  // 点击盒子以外的地方弹窗关闭
  $(document).mousedown(function(e) {
    if ($(e.target).parent(".imgshow").length == 0) {
      $(".maskweap").fadeOut(); //弹窗关闭
    }
  });
})

const swing = () => {
  $switchWrapper.addClass('swing');

  setTimeout(() => {
    $switchWrapper.removeClass('swing');
  }, 1000);
};

const randomAnimal = () => {
  let arr = ['../img/bear_wgyw8f.png','../img/bunny_kpofl4.png'];
  return arr[Math.floor(Math.random() * arr.length)];
};

$('.switch').on('click', () => {
  if ($body.hasClass('lightsOn')) {
    $body.removeClass('lightsOn').addClass('lightsOff');
    $pupil.removeClass('center');
    $animalBody.addClass('hide');
    $explainGood.removeClass('explain').addClass('hide');
  } else {
    $('.lightsOff').find('.eye').css('transform', 'rotate(0deg)'); 
    $body.addClass('lightsOn').removeClass('lightsOff');
    $pupil.addClass('center');
    $animalBody.removeClass('hide').attr('src', randomAnimal());
    $explainGood.removeClass('hide').addClass('explain');
  }

  swing();
});

$('.string').on('mouseenter', swing);


//the following code is referencing from Codewoofy: https://codepen.io/Codewoofy/pen/VeBJEP
$body.mousemove(event => {
  event.preventDefault();
  let x = ($eye.offset().left) + ($eye.width() / 2);
  let y = ($eye.offset().top) + ($eye.height() / 2);
  let rad = Math.atan2(event.pageX - x, event.pageY - y);
  let rot = (rad * (180 / Math.PI) * -1) + 180;
  $('.lightsOff').find('.eye').css({'transform': 'rotate(' + rot + 'deg)'});
});