# BlurBehindActivity
仿ios给activity背景设置模糊度，可以设置任意透明度（只需要一行代码简单集成）
安卓模糊背景，半透明背景，任意透明度背景


#使用步骤，只需下面简单2步。
##1，把项目中的qclCopy文件夹里的类直接复制到你的项目就行

![image](https://github.com/qiushi123/BlurBehindActivity/blob/master/images_qcl/qcl.png?raw=true)
##2，完成好第一步就直接使用了
###比如你从MainActivity跳转到activity2
	public class MainActivity extends Activity {
		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.activity_main);
			findViewById(R.id.dummy_button).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {

					BlurBehind.getInstance().execute(MainActivity.this, new OnBlurCompleteListener() {
						@Override
						public void onBlurComplete() {
							Intent intent = new Intent(MainActivity.this, MainActivity2.class);
							intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);

							startActivity(intent);
						}
					});
				}
			});

		}
	}
	
###MainActivity2 只需要添加下面简单一行代码就可以设置模糊效果（也可以设置任意透明度）
	public class MainActivity2 extends Activity {
		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.activity_blurred);

			BlurBehind.getInstance()//在你需要添加模糊或者透明的背景中只需要设置这几行简单的代码就可以了
					.withAlpha(50)
					.withFilterColor(Color.parseColor("#0075c0"))
					.setBackground(this);
		}
	}

#下面是效果图

![image](https://github.com/qiushi123/BlurBehindActivity/blob/master/images_qcl/blur-behind-before.png?raw=true) ![image](https://github.com/qiushi123/BlurBehindActivity/blob/master/images_qcl/blur-behind-after.png?raw=true)
