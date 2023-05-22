package com.earth.angel.view.clip;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.earth.angel.R;
import com.earth.angel.util.BitmapUtil;


/**
 * 头像上传原图裁剪容器
 */
public class ClipViewLayout extends RelativeLayout {
    //动作标志：无
    private static final int NONE = 0;
    //动作标志：拖动
    private static final int DRAG = 1;
    //动作标志：缩放
    private static final int ZOOM = 2;
    //用于存放矩阵的9个值
    private final float[] matrixValues = new float[9];
    //裁剪原图
    private ImageView imageView;
    //裁剪框
    private ClipView clipView;
    //裁剪框水平方向间距，xml布局文件中指定
    private float mHorizontalPadding;
    //裁剪框垂直方向间距，计算得出
    private float mVerticalPadding;
    //图片缩放、移动操作矩阵
    private Matrix matrix = new Matrix();
    //图片原来已经缩放、移动过的操作矩阵
    private Matrix savedMatrix = new Matrix();
    //初始化动作标志
    private int mode = NONE;
    //记录起始坐标
    private PointF start = new PointF();
    //记录缩放时两指中间点坐标
    private PointF mid = new PointF();
    private float oldDist = 1f;
    //最小缩放比例
    private float minScale;
    //最大缩放比例
    private float maxScale = 4;

    private Bitmap mBitmap;
    private Bitmap cropBitmap;
    private int mDegreesRotated;


    public ClipViewLayout(Context context) {
        this(context, null);
    }

    public ClipViewLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ClipViewLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public static Bitmap rotateBitmap(Bitmap bitmap, Rect rect, int degrees) {
        Matrix matrix = new Matrix();
        matrix.setRotate((float) degrees);
        Bitmap newBitmap = Bitmap.createBitmap(bitmap, rect.left, rect.top, rect.width(), rect.height(), matrix, false);
        bitmap.recycle();
        return newBitmap;
    }

    /**
     * 图片缩放到指定宽高
     * <p/>
     * 非等比例压缩，图片会被拉伸
     *
     * @param bitmap 源位图对象
     * @param w      要缩放的宽度
     * @param h      要缩放的高度
     * @return 新Bitmap对象
     */
    public static Bitmap zoomBitmap(Bitmap bitmap, int w, int h) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Matrix matrix = new Matrix();
        float scaleWidth = ((float) w / width);
        float scaleHeight = ((float) h / height);
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap newBmp = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, false);
        return newBmp;
    }

    //初始化控件自定义的属性
    public void init(Context context, AttributeSet attrs) {
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.ClipViewLayout);

        //获取剪切框距离左右的边距, 默认为50dp
        mHorizontalPadding = array.getDimensionPixelSize(R.styleable.ClipViewLayout_mHorizontalPadding,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, getResources().getDisplayMetrics()));
        //获取裁剪框边框宽度，默认1dp
        int clipBorderWidth = array.getDimensionPixelSize(R.styleable.ClipViewLayout_clipBorderWidth,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, getResources().getDisplayMetrics()));
        //裁剪框类型(圆或者矩形)
        int clipType = array.getInt(R.styleable.ClipViewLayout_clipType, 1);

        //回收
        array.recycle();
        clipView = new ClipView(context);
        //设置裁剪框类型
        clipView.setClipType(clipType == 1 ? ClipView.ClipType.CIRCLE : ClipView.ClipType.RECTANGLE);
        //设置剪切框边框
        clipView.setClipBorderWidth(clipBorderWidth);
        //设置剪切框水平间距
        clipView.setmHorizontalPadding(mHorizontalPadding);
        addViews();
    }

    public void addViews() {
        imageView = new ImageView(getContext());
        //相对布局布局参数
        android.view.ViewGroup.LayoutParams lp = new LayoutParams(
                android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                android.view.ViewGroup.LayoutParams.MATCH_PARENT);
        this.addView(imageView, lp);
        this.addView(clipView, lp);
    }

    public void resetParams() {
        removeAllViews();
        mDegreesRotated = 0;
        mBitmap = null;
        cropBitmap = null;
        matrix = new Matrix();
        savedMatrix = new Matrix();
    }

    public void setImageBitmap(final Bitmap bitmap) {
        ViewTreeObserver observer = imageView.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            public void onGlobalLayout() {
                initSrcPic(bitmap);
                imageView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }
        });
    }

    /**
     * 初始化图片
     * step 1: decode 出 720*1280 左右的照片  因为原图可能比较大 直接加载出来会OOM
     * step 2: 将图片缩放 移动到imageView 中间
     */
    public void initSrcPic(Bitmap bitmap) {

        this.mBitmap = bitmap;
        //图片的缩放比
        float scale = 0;
        if (bitmap.getWidth() >= bitmap.getHeight()) {//宽图
            scale = (float) imageView.getWidth() / bitmap.getWidth();
            //如果高缩放后小于裁剪区域 则将裁剪区域与高的缩放比作为最终的缩放比
            Rect rect = clipView.getClipRect();
            //高的最小缩放比
            minScale = rect.height() / (float) bitmap.getHeight();
            if (scale < minScale) {
                scale = minScale;
            }
        } else {//高图
            //高的缩放比
            scale = (float) imageView.getHeight() / bitmap.getHeight();
            //如果宽缩放后小于裁剪区域 则将裁剪区域与宽的缩放比作为最终的缩放比
            Rect rect = clipView.getClipRect();
            //宽的最小缩放比
            minScale = rect.width() / (float) bitmap.getWidth();
            if (scale < minScale) {
                scale = minScale;
            }
        }
        // 缩放
        matrix.postScale(scale, scale);
        // 平移,将缩放后的图片平移到imageview的中心
        //imageView的中心x
        int midX = imageView.getWidth() / 2;
        //imageView的中心y
        int midY = imageView.getHeight() / 2;
        //bitmap的中心x
        int imageMidX = (int) (bitmap.getWidth() * scale / 2);
        //bitmap的中心y
        int imageMidY = (int) (bitmap.getHeight() * scale / 2);
        matrix.postTranslate(midX - imageMidX, midY - imageMidY);
        imageView.setScaleType(ImageView.ScaleType.MATRIX);
        imageView.setImageMatrix(matrix);
        imageView.setImageBitmap(bitmap);
    }

    /**
     * 查询图片旋转角度
     */


    public void rotateImage() {
        if (this.mBitmap != null) {
            Matrix matrix = new Matrix();
            //imageView的中心x
            int midX = imageView.getWidth() / 2;
            //imageView的中心y
            int midY = imageView.getHeight() / 2;
            mBitmap = BitmapUtil.INSTANCE.rotateBitmap(mBitmap, 90, midX, midY);

            //图片的缩放比
            float scale = 0;
            if (mBitmap.getWidth() >= mBitmap.getHeight()) {//宽图
                scale = (float) imageView.getWidth() / mBitmap.getWidth();
                //如果高缩放后小于裁剪区域 则将裁剪区域与高的缩放比作为最终的缩放比
                Rect rect = clipView.getClipRect();
                //高的最小缩放比
                minScale = rect.height() / (float) mBitmap.getHeight();
                if (scale < minScale) {
                    scale = minScale;
                }
            } else {//高图
                //高的缩放比
                scale = (float) imageView.getHeight() / mBitmap.getHeight();
                //如果宽缩放后小于裁剪区域 则将裁剪区域与宽的缩放比作为最终的缩放比
                Rect rect = clipView.getClipRect();
                //宽的最小缩放比
                minScale = rect.width() / (float) mBitmap.getWidth();
                if (scale < minScale) {
                    scale = minScale;
                }
            }
            // 缩放
            matrix.postScale(scale, scale);
            // 平移,将缩放后的图片平移到imageview的中心

            //bitmap的中心x
            int imageMidX = (int) (mBitmap.getWidth() * scale / 2);
            //bitmap的中心y
            int imageMidY = (int) (mBitmap.getHeight() * scale / 2);
            matrix.postTranslate(midX - imageMidX, midY - imageMidY);
            imageView.setImageMatrix(matrix);
            imageView.setImageBitmap(mBitmap);

            /*imageView.setPivotX(imageView.getWidth()/2);
            imageView.setPivotY(imageView.getHeight()/2);//支点在图片中心
            this.mDegreesRotated += degrees;
            this.mDegreesRotated %= 360;
            imageView.setRotation(mDegreesRotated);*/
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                savedMatrix.set(matrix);
                //设置开始点位置
                start.set(event.getX(), event.getY());
                mode = DRAG;
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                //开始放下时候两手指间的距离
                oldDist = spacing(event);
                if (oldDist > 10f) {
                    savedMatrix.set(matrix);
                    midPoint(mid, event);
                    mode = ZOOM;
                }
                break;
            case MotionEvent.ACTION_UP:
                break;
            case MotionEvent.ACTION_POINTER_UP:
                mode = NONE;
                break;
            case MotionEvent.ACTION_MOVE:
                if (mode == DRAG) { //拖动
                    matrix.set(savedMatrix);
                    float dx = event.getX() - start.x;
                    float dy = event.getY() - start.y;
                    mVerticalPadding = clipView.getClipRect().top;
                    matrix.postTranslate(dx, dy);
                    //检查边界
                    checkBorder();
                } else if (mode == ZOOM) { //缩放
                    //缩放后两手指间的距离
                    float newDist = spacing(event);
                    if (newDist > 10f) {
                        //手势缩放比例
                        float scale = newDist / oldDist;
                        if (scale < 1) { //缩小
                            if (getScale() > minScale) {
                                matrix.set(savedMatrix);
                                mVerticalPadding = clipView.getClipRect().top;
                                matrix.postScale(scale, scale, mid.x, mid.y);
                                //缩放到最小范围下面去了，则返回到最小范围大小
                                while (getScale() < minScale) {
                                    //返回到最小范围的放大比例
                                    scale = 1 + 0.01F;
                                    matrix.postScale(scale, scale, mid.x, mid.y);
                                }
                            }
                            //边界检查
                            checkBorder();
                        } else { //放大
                            if (getScale() <= maxScale) {
                                matrix.set(savedMatrix);
                                mVerticalPadding = clipView.getClipRect().top;
                                matrix.postScale(scale, scale, mid.x, mid.y);
                            }
                        }
                    }
                }
                imageView.setImageMatrix(matrix);
                break;
        }
        return true;
    }

    /**
     * 根据当前图片的Matrix获得图片的范围
     */
    private RectF getMatrixRectF(Matrix matrix) {
        RectF rect = new RectF();
        Drawable d = imageView.getDrawable();
        if (null != d) {
            rect.set(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
            matrix.mapRect(rect);
        }
        return rect;
    }

    /**
     * 边界检测
     */
    private void checkBorder() {
        RectF rect = getMatrixRectF(matrix);
        float deltaX = 0;
        float deltaY = 0;
        int width = imageView.getWidth();
        int height = imageView.getHeight();
        // 如果宽或高大于屏幕，则控制范围 ; 这里的0.001是因为精度丢失会产生问题，但是误差一般很小，所以我们直接加了一个0.01
        if (rect.width() >= width - 2 * mHorizontalPadding) {
            if (rect.left > mHorizontalPadding) {
                deltaX = -rect.left + mHorizontalPadding;
            }
            if (rect.right < width - mHorizontalPadding) {
                deltaX = width - mHorizontalPadding - rect.right;
            }
        }
        if (rect.height() >= height - 2 * mVerticalPadding) {
            if (rect.top > mVerticalPadding) {
                deltaY = -rect.top + mVerticalPadding;
            }
            if (rect.bottom < height - mVerticalPadding) {
                deltaY = height - mVerticalPadding - rect.bottom;
            }
        }
        matrix.postTranslate(deltaX, deltaY);
    }

    /**
     * 获得当前的缩放比例
     */
    public final float getScale() {
        matrix.getValues(matrixValues);
        return matrixValues[Matrix.MSCALE_X];
    }

    /**
     * 多点触控时，计算最先放下的两指距离
     */
    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    /**
     * 多点触控时，计算最先放下的两指中心坐标
     */
    private void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }

    /**
     * 获取剪切图
     */
    public Bitmap clip() {
        imageView.setDrawingCacheEnabled(true);
        imageView.buildDrawingCache();
        Rect rect = clipView.getClipRect();
        Bitmap cropBitmap = null;
//        Bitmap zoomedCropBitmap = null;
        try {
            if (mDegreesRotated > 0) {
                cropBitmap = rotateBitmap(imageView.getDrawingCache(), rect, mDegreesRotated);
            } else {
                cropBitmap = Bitmap.createBitmap(imageView.getDrawingCache(), rect.left, rect.top, rect.width(), rect.height());
            }
//            zoomedCropBitmap = zoomBitmap(cropBitmap, 200, 200);
        } catch (Exception e) {
            e.printStackTrace();
        }
//        if (cropBitmap != null) {
//            cropBitmap.recycle();
//        }
        // 释放资源
        imageView.destroyDrawingCache();
        if (mBitmap.getWidth() < 300 || mBitmap.getHeight() <300) {
            return mBitmap;
        }
        return cropBitmap;
    }
/*    public ImageClipData clip() {
        ImageClipData clipData = new ImageClipData();
        imageView.setDrawingCacheEnabled(true);
        imageView.buildDrawingCache();
        Rect rect = clipView.getClipRect();
        try {
            Rect actualRect = getActualCropRect(rect);
            cropBitmap = Bitmap.createBitmap(mBitmap, actualRect.left,
                    actualRect.top, actualRect.width(), actualRect.height());
            clipData.setBitmap(mBitmap);
            clipData.setCropBitmap(cropBitmap);
            clipData.setCrop_x(actualRect.left);
            clipData.setCrop_y(actualRect.top);
            clipData.setCrop_witdh(actualRect.width());
            clipData.setCrop_height(actualRect.height());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return clipData;
    }*/

    public Rect getActualCropRect(Rect clipRect) {
        if (mBitmap != null) {
            RectF displayedImageRect = getMatrixRectF(matrix);
            // Get the scale factor between the actual Bitmap dimensions and the displayed dimensions for width.
            final float actualImageWidth = mBitmap.getWidth();
            final float displayedImageWidth = displayedImageRect.width();
            final float scaleFactorWidth = actualImageWidth / displayedImageWidth;

            // Get the scale factor between the actual Bitmap dimensions and the displayed dimensions for height.
            final float actualImageHeight = mBitmap.getHeight();
            final float displayedImageHeight = displayedImageRect.height();
            final float scaleFactorHeight = actualImageHeight / displayedImageHeight;

            // Get crop window position relative to the displayed image.
            final float displayedCropLeft = clipRect.left - displayedImageRect.left;
            final float displayedCropTop = clipRect.top - displayedImageRect.top;
            final float displayedCropWidth = clipRect.width();
            final float displayedCropHeight = clipRect.height();

            // Scale the crop window position to the actual size of the Bitmap.
            float actualCropLeft = displayedCropLeft * scaleFactorWidth;
            float actualCropTop = displayedCropTop * scaleFactorHeight;
            float actualCropRight = actualCropLeft + displayedCropWidth * scaleFactorWidth;
            float actualCropBottom = actualCropTop + displayedCropHeight * scaleFactorHeight;

            // Correct for floating point errors. Crop rect boundaries should not exceed the source Bitmap bounds.
            actualCropLeft = Math.max(0f, actualCropLeft);
            actualCropTop = Math.max(0f, actualCropTop);
            actualCropRight = Math.min(mBitmap.getWidth(), actualCropRight);
            actualCropBottom = Math.min(mBitmap.getHeight(), actualCropBottom);

            return new Rect((int) actualCropLeft, (int) actualCropTop, (int) actualCropRight, (int) actualCropBottom);
        } else {
            return null;
        }
    }


}
