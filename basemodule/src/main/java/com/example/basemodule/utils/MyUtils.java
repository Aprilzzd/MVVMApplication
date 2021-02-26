package com.example.basemodule.utils;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.*;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import com.blankj.utilcode.util.ToastUtils;
import com.blankj.utilcode.util.Utils;
import com.example.basemodule.R;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;


/**
 * @author leeandy007
 * @Desc: 封装android公用工具类 （SharedPreferences，Toast， ProgressDialog，Dialog ）
 * @version :
 *
 */
@SuppressWarnings("deprecation")
public class MyUtils {

	private final static SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm");
    public final static SimpleDateFormat sdf1=new SimpleDateFormat("HH:mm:ss");
	private final static SimpleDateFormat sdf2=new SimpleDateFormat("mm:ss");

	/**
	 * DIP -> PX 转换
	 * @param dipValue
	 * @return
	 */
	public static int dip2px(float dipValue) {
		final float scale = Utils.getApp().getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}

    public static int sp2px(float spValue) {
        final float fontScale = Utils.getApp().getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

	/**
	 * PX -> DIP 转换
	 * @param context
	 * @param pxValue
	 * @return
	 */
	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}
	/**
	 * 返回版本号（String）
	 * @param context
	 * @return
	 */
	public static String getVersionName(Context context) {
		String versionName = "";
		try {
			PackageInfo info = context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0);
			versionName = info.versionName;
		} catch (Exception e) {

		}
		return versionName;
	}

	public static String removeStr(String s, String string, int i){
		if(i==1){
			int j=s.indexOf(string);
			s=s.substring(0, j)+s.substring(j+1);
			return s;
		}else{
			int j=s.indexOf(string);
			i--;
			return s.substring(0, j+1)+removeStr(s.substring(j+1), string, i);
		}
	}

	public static String getChannelId(Context context){
		ApplicationInfo appInfo = null;
		try {
			appInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(),
                    PackageManager.GET_META_DATA);
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}
		if (appInfo!=null){
			return appInfo.metaData.getInt("APP_CHANNEL")+"";
		}else {
			return "1";
		}
	}

	public static String getChannelName(Context context){
		ApplicationInfo appInfo = null;
		try {
			appInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(),
					PackageManager.GET_META_DATA);
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}
		if (appInfo!=null){
			return appInfo.metaData.getString("TD_CHANNEL_ID");
		}else {
			return "null";
		}
	}

	public static boolean inMainProcess(Context context) {
		String mainProcessName = context.getApplicationInfo().processName;
		String processName = getProcessName();
//		Log.e("==",mainProcessName+"=="+processName);
		return TextUtils.equals(mainProcessName, processName);
	}

	/**
	 * 获取当前进程名
	 */
	private static String getProcessName() {
		BufferedReader reader = null;
		try {
			File file = new File("/proc/" + android.os.Process.myPid() + "/" + "cmdline");
			reader = new BufferedReader(new FileReader(file));
			return reader.readLine().trim();
		} catch (IOException e) {
			return null;
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static void weixinPay(Context context, String data){
		try {
			JSONObject object=new JSONObject(data);
			IWXAPI msgApi = WXAPIFactory.createWXAPI(context, Constants.WXAPPID);
			PayReq req = new PayReq();
			req.appId			= Constants.WXAPPID;
			req.partnerId		= object.getString("mch_id");
			req.prepayId		= object.getString("prepay_id");
			req.nonceStr		= object.getString("nonce_str");
			req.timeStamp		= object.getString("timestamp");
			req.packageValue	= "Sign=WXPay";
			req.sign			= object.getString("sign");
			msgApi.sendReq(req);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public static String timeChange(long time){
		return sdf.format(new Date(time));
	}

	public static double formatDouble(double d){
		if (d<0){
			d=0.0;
		}else {
			d=BigDecimal.valueOf(d).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
		}
		return d;
	}

	public static double formatDouble1(double d){
		if (d<=0){
			d=0.0;
		}else {
			d=BigDecimal.valueOf(d).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
		}
		return d;
	}

	public static void loginOut(final Activity context, ACache aCache) {
		aCache.remove("phone");
		aCache.remove("token");
		//如果ctx等于空或者isFinishing
		if (context == null || context.isFinishing()) return;
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final View view = ((ViewGroup)context.getWindow().getDecorView()).getChildAt(0);
                Object tag = view.getTag();
                boolean dialogShowTag = tag != null && (boolean)tag;
                if (dialogShowTag) {
                    return;
                }
                view.setTag(true);
                new AlertDialog.Builder(context).setMessage("您的登录已过期，请重新登录")
                        .setPositiveButton("去登录", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
//                                context.startActivity(new Intent(context, LoginActivity.class));
                            }
                        }).setNegativeButton(R.string.cancel,null).setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                view.setTag(false);
                            }
                        }).show();
            }
		});
	}

	public static void setDrawableLeft(int id, TextView textView){
		Drawable drawable= Utils.getApp().getResources().getDrawable(id);
		drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
		textView.setCompoundDrawables(drawable,null,null,null);
	}

	public static void setDrawableRight(int id, TextView textView){
		Drawable drawable= Utils.getApp().getResources().getDrawable(id);
		drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
		textView.setCompoundDrawables(null,null,drawable,null);
	}

	public static void setDrawableTop(int id, TextView textView){
		Drawable drawable= Utils.getApp().getResources().getDrawable(id);
		drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
		textView.setCompoundDrawables(null,drawable,null,null);
	}

	public static void setDrawableNull(TextView textView){
		textView.setCompoundDrawables(null,null,null,null);
	}

	public static int getNavigationBarHeight(Context context) {
		int resourceId;
		int rid = context.getResources().getIdentifier("config_showNavigationBar", "bool", "android");
		if (rid!=0){
			resourceId = context.getResources().getIdentifier("navigation_bar_height", "dimen"
                    , "android");
			Log.e("--", "getNavigationBarHeight: "+resourceId);
			Log.e("--", "getNavigationBarHeight: "+context.getResources().getDimensionPixelSize(resourceId));
			return context.getResources().getDimensionPixelSize(resourceId);
		}else
			return 0;
	}

	public static boolean checkNavigationBar(Context activity) {
		//通过判断设备是否有返回键、菜单键(不是虚拟键,是手机屏幕外的按键)来确定是否有navigation bar
		boolean hasMenuKey = ViewConfiguration.get(activity)
				.hasPermanentMenuKey();
		boolean hasBackKey = KeyCharacterMap
				.deviceHasKey(KeyEvent.KEYCODE_BACK);
		if (!hasMenuKey && !hasBackKey) {
			return true;
		}
		return false;
	}

	public static void byte2File(byte[] buf, String filePath) {
		BufferedOutputStream bos = null;
		FileOutputStream fos = null;
		File file = null;
		try {
			file = new File(filePath);
			fos = new FileOutputStream(file);
			bos = new BufferedOutputStream(fos);
			bos.write(buf);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (bos != null) {
				try {
					bos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static boolean FlymeSetStatusBarLightMode(Window window, boolean dark) {
		boolean result = false;
		if (window != null) {
			try {
				WindowManager.LayoutParams lp = window.getAttributes();
				Field darkFlag = WindowManager.LayoutParams.class
						.getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON");
				Field meizuFlags = WindowManager.LayoutParams.class
						.getDeclaredField("meizuFlags");
				darkFlag.setAccessible(true);
				meizuFlags.setAccessible(true);
				int bit = darkFlag.getInt(null);
				int value = meizuFlags.getInt(lp);
				if (dark) {
					value |= bit;
				} else {
					value &= ~bit;
				}
				meizuFlags.setInt(lp, value);
				window.setAttributes(lp);
				result = true;
			} catch (Exception e) {

			}
		}
		return result;
	}

	public static boolean MIUISetStatusBarLightMode(Window window, boolean dark) {
		boolean result = false;
		if (window != null) {
			Class clazz = window.getClass();
			try {
				int darkModeFlag = 0;
				Class layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
				Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
				darkModeFlag = field.getInt(layoutParams);
				Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
				if (dark) {//状态栏透明且黑色字体
					extraFlagField.invoke(window, darkModeFlag, darkModeFlag);
				} else {//清除黑色字体
					extraFlagField.invoke(window, 0, darkModeFlag);
				}
				result = true;
			} catch (Exception e) {

			}
		}
		return result;
	}

	public static void setFullStatusBar(Activity activity, boolean dark){
		View decorView = activity.getWindow().getDecorView();
		if (Build.VERSION.SDK_INT >= 23) {
			MIUISetStatusBarLightMode(activity.getWindow(), !dark);
			FlymeSetStatusBarLightMode(activity.getWindow(), !dark);
			int option=0;
			if (dark){
				option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
						| View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
			}else {
				option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
						| View.SYSTEM_UI_FLAG_LAYOUT_STABLE|View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
			}
			decorView.setSystemUiVisibility(option);
			activity.getWindow().setStatusBarColor(Color.parseColor("#00000000"));
		}else if (Build.VERSION.SDK_INT >= 21){
			int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
					| View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
			decorView.setSystemUiVisibility(option);
			if (dark)
                activity.getWindow().setStatusBarColor(Color.parseColor("#00000000"));
			else
                activity.getWindow().setStatusBarColor(Color.parseColor("#4f000000"));
		}
	}

	public static void setStatusBar(Activity activity, boolean dark){
		View decorView = activity.getWindow().getDecorView();
		if (Build.VERSION.SDK_INT >= 23) {
			MIUISetStatusBarLightMode(activity.getWindow(), !dark);
			FlymeSetStatusBarLightMode(activity.getWindow(), !dark);
			int option=0;
			if (dark){
				option = View.SYSTEM_UI_FLAG_VISIBLE;
			}else {
				option = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
			}
			decorView.setSystemUiVisibility(option);
			activity.getWindow().setStatusBarColor(Color.parseColor("#00000000"));
		}else if (Build.VERSION.SDK_INT >= 21){
			int option = View.SYSTEM_UI_FLAG_VISIBLE;
			decorView.setSystemUiVisibility(option);
			if (dark)
				activity.getWindow().setStatusBarColor(Color.parseColor("#00000000"));
			else
				activity.getWindow().setStatusBarColor(Color.parseColor("#4f000000"));
		}
	}

    public static String getTime(long i){
		return sdf1.format(new Date(i- TimeZone.getDefault().getRawOffset()));
	}

	public static String getTime1(long i){
		return sdf2.format(new Date(i- TimeZone.getDefault().getRawOffset()));
	}

	public static boolean noLogin(){
		String token= ACache.get(Utils.getApp()).getAsString("token");
		return token==null||token.equals("");
	}

	public static String formatTime(String time){
        return time.substring(0,19).replace("T"," ");
    }

    public static boolean check(String phone,String code,String pass,String pass1){
	    if (phone!=null){
            if (!phone.matches(Constants.PHONERULE)){
                ToastUtils.showShort("请输入正确的手机号");
                return false;
            }
        }
        if (code!=null){
            if (TextUtils.isEmpty(code)){
                ToastUtils.showShort("请输入正确的验证码");
                return false;
            }
        }
        if (pass!=null){
            if (!pass.matches(Constants.PASSRULE)){
                ToastUtils.showShort("请输入正确格式的密码");
                return false;
            }
        }
        if (pass1!=null){
            if (!pass1.equals(pass)) {
                ToastUtils.showShort("两次输入的密码不一致");
                return false;
            }
        }
        return true;
    }

    public static void showSoftInput(final Context context, final View view){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(view, 0);
//                imm.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
            }
        }, 100);
    }

    public static String camera(Fragment fragment){
        File img = new File(Constants.IMAGE_PATH);
        if (!img.exists()) {
            img.mkdirs();
        }
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        String imagePath = Constants.IMAGE_PATH + System.currentTimeMillis() + ".jpg";
        File file = new File(imagePath);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri contentUri = FileProvider.getUriForFile(fragment.getContext(), fragment.getContext()
                    .getPackageName()+".fileProvider", file);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri);
        } else {
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
        }
        fragment.startActivityForResult(intent, 1);
        return imagePath;
    }

    public static void album(Fragment fragment){
        Intent in = new Intent(Intent.ACTION_PICK, null);
        in.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                "image/*");
        fragment.startActivityForResult(in, 2);
    }

    public static void getMedia(Fragment fragment){
        Intent in = new Intent(Intent.ACTION_PICK, null);
        in.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                "video/*;image/*");
        fragment.startActivityForResult(in, 2);
    }

    public static String compress(Activity activity,int requestCode,Intent data,String imagePath,MyViewCallback callback){
        String outPath = activity.getExternalCacheDir().getPath() + File.separator + "123.jpg";
        switch (requestCode) {
            case 1:// 拍照
                File temp = new File(imagePath);
                if (temp.exists()) {
                    ImageFactory.getImage(imagePath, outPath);
                    callback.callback(0,outPath);
                }
                break;
            case 2:// 相册
                if (data != null) {
                    Cursor cursor = activity.getContentResolver().query(data.getData(), null, null
                            , null, null);
                    String imgPath;
                    if (cursor == null) {
                        imgPath = data.getData().getPath();
                    } else {
                        cursor.moveToFirst();
                        imgPath = cursor.getString(1);
                        cursor.close();
                    }
                    ImageFactory.getImage(imgPath, outPath);
                    callback.callback(0,outPath);
                }
                break;
        }
        return outPath;
    }

    public static void copy(File source, File target) {
        FileInputStream fileInputStream = null;
        FileOutputStream fileOutputStream = null;
        try {
            fileInputStream = new FileInputStream(source);
            fileOutputStream = new FileOutputStream(target);
            byte[] buffer = new byte[1024];
            while (fileInputStream.read(buffer) > 0) {
                fileOutputStream.write(buffer);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fileInputStream != null) {
                    fileInputStream.close();
                }
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static String getMonth(int month){
        return month<9?"0"+(month+1):""+(month+1);
    }

    public static String getSex(String sex){
        if (sex!=null){
            if (sex.equals("1"))
                return "男";
            if (sex.equals("2"))
                return "女";
        }
        return "请选择";
    }

    public static String getSexCode(String sex){
        if (sex!=null){
            if (sex.equals("男"))
                return "1";
            if (sex.equals("女"))
                return "2";
        }
        return "0";
    }

    public static void loginOut(ACache mCache){
        mCache.remove("token");
        mCache.put("uid","0");
    }

    public static int getUid(){
	    String s= ACache.get(Utils.getApp()).getAsString("uid");
	    if (s==null)
	        return 0;
	    else
	        return Integer.valueOf(s);
    }

    public static String getTime(String s){
	    if (s==null)
	        return "";
	    return s.substring(0,10);
    }

    public static MultipartBody.Part getImagePart(String path){
	    if (TextUtils.isEmpty(path)) return null;
	    File file=new File(path);
        RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
        return MultipartBody.Part.createFormData("file", file.getName(), requestFile);
    }

    public static MultipartBody.Part getVideoPart(String path){
        if (TextUtils.isEmpty(path)) return null;
        File file=new File(path);
        RequestBody requestFile = RequestBody.create(MediaType.parse("video/*"), file);
        return MultipartBody.Part.createFormData("file", file.getName(), requestFile);
    }

    public static RequestBody getRequestBody(String s){
	    return RequestBody.create(MediaType.parse("multipart/form-data"), s);
    }

    public static void install(Activity context,File mFile){
        Intent intent1 = new Intent(Intent.ACTION_VIEW);
        intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N) {
            intent1.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri uri1 = FileProvider.getUriForFile(context, context.getPackageName() + ".fileProvider", mFile);
            intent1.setDataAndType(uri1, "application/vnd.android.package-archive");
        } else {
            intent1.setDataAndType(Uri.fromFile(mFile), "application/vnd.android.package-archive");
        }
        try {
            context.startActivity(intent1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getPath(Context context,Uri uri) {
        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
        String imgPath;
        if (cursor == null) {
            imgPath = uri.getPath();
        } else {
            cursor.moveToFirst();
            imgPath = cursor.getString(1);
            cursor.close();
        }
        return imgPath;
    }

    public static String getRealFilePath(Context context, Uri uri) {
        if (null == uri) return "";
        final String scheme = uri.getScheme();
        String realPath = "";
        if (scheme == null)
            realPath = uri.getPath();
        else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            realPath = uri.getPath();
        } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            Cursor cursor = context.getContentResolver().query(uri,
                    new String[]{MediaStore.Images.ImageColumns.DATA},
                    null, null, null);
            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    if (index > -1) {
                        realPath = cursor.getString(index);
                    }
                }
                cursor.close();
            }else {
                realPath = uri.getPath();
            }
        }
        if (TextUtils.isEmpty(realPath)) {
            String uriString = uri.toString();
            int index = uriString.lastIndexOf("/");
            String imageName = uriString.substring(index);
            File storageDir;

            storageDir = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES);
            File file = new File(storageDir, imageName);
            if (file.exists()) {
                realPath = file.getAbsolutePath();
            } else {
                storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                File file1 = new File(storageDir, imageName);
                realPath = file1.getAbsolutePath();
            }
        }
        return realPath;
    }

    public static Map<String,String> getPageMap(int pageNum,int pageSize){
        Map<String,String> map=new HashMap<>();
        map.put("pageNum",pageNum+"");
        map.put("pageSize",pageSize+"");
	    return map;
    }

    public static String getJson(Context context,String fileName) {

        StringBuilder stringBuilder = new StringBuilder();
        try {
            AssetManager assetManager = context.getAssets();
            BufferedReader bf = new BufferedReader(new InputStreamReader(
                    assetManager.open(fileName)));
            String line;
            while ((line = bf.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

    public static String setCity(String city){
        if (city==null) return "";
        switch (city){
            case "北京市":
                break;
            case "天津市":
                break;
            case "重庆市":
                break;
            case "上海市":
                break;
            case "null":
                break;
            case "暂无":
                break;
            default:
                return "-"+city+"-";
        }
        return "-";
    }
}
