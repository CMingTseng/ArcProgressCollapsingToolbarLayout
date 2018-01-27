package com.chenyu.designlibrarysample.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.chenyu.designlibrarysample.R;
import com.chenyu.designlibrarysample.utils.Utils;


//FIXME  FIXME how set color & style .... by XML ?

/**
 * http://tech.dianwoda.com/2016/11/01/androidzi-ding-yi-kong-jian-shi-xian-yan-se-jian-bian-shi-yuan-xing-jin-du/
 * Created by bruce on 11/6/14.
 */
public class ArcProgress extends View {
    private final static String TAG = "ArcProgress";
    private final static int[] DEFAULT_GRADIENT_COLOR = new int[]{Color.RED, Color.YELLOW, Color.GREEN, Color.RED};
    private final static int DEFAULT_DIAMETER = 200;
    private final static int DEFAULT_ARCANGLE = 270;
    private final static int DEFAULT_ROTAT_ARCANGLE = 135;
    private final static float DEFAULT_STAGE = 0.5f;
    private final static float DEFAULT_MAX = 100;
    private final static float DEFAULT_MIN = 0;
    private final static int DEFAULT_DEGREE_COLOR = Color.BLACK;
    private final static int DEFAULT_ARC_BACKGROUND_COLOR = Color.GRAY;
    private Paint mBackCirclePaint;
    private Matrix mRotateMatrix;
    protected Paint mTextPaint;
    private SweepGradient mSweepGradient;
    private RectF mRectF = new RectF();
    private float currentAngle = 0;
    private float lastAngle;
    private float strokeWidth;
    private float suffixTextSize;
    private float bottomTextSize;
    private String bottomText;
    private float textSize;
    private int textColor;
    private int mProgress = 0;
    private int max;
    private int[] mMasterFinishGradientColor = DEFAULT_GRADIENT_COLOR;
    private int[] mKeepingGradientColor;
    private int[] mUnfinishGradientColor = new int[]{Color.GRAY, Color.YELLOW};
    private int[] mSlaveFinishGradientColor = DEFAULT_GRADIENT_COLOR;
    private int mArcBackgroundColor = DEFAULT_ARC_BACKGROUND_COLOR;
    private float mArcAngle;
    private float mStageChange = DEFAULT_STAGE;
    private String suffixText = "";//FIXME how set default_suffix_text
    private float suffixTextPadding;

    private float arcBottomHeight;

    private float circleBorderWidth;
    private float circlePadding;
    private final int default_text_color = Color.rgb(66, 145, 241);
    private final float default_suffix_text_size;
    private final float default_suffix_padding;
    private final float default_bottom_text_size;
    private float mStrokeWidth;
    private final String default_suffix_text;
    private final int default_max = 100;
    private float default_text_size;
    private final int min_size;

    private static final String INSTANCE_STATE = "saved_instance";
    private static final String INSTANCE_STROKE_WIDTH = "stroke_width";
    private static final String INSTANCE_SUFFIX_TEXT_SIZE = "suffix_text_size";
    private static final String INSTANCE_SUFFIX_TEXT_PADDING = "suffix_text_padding";
    private static final String INSTANCE_BOTTOM_TEXT_SIZE = "bottom_text_size";
    private static final String INSTANCE_BOTTOM_TEXT = "bottom_text";
    private static final String INSTANCE_TEXT_SIZE = "text_size";
    private static final String INSTANCE_TEXT_COLOR = "text_color";
    private static final String INSTANCE_PROGRESS = "mProgress";
    private static final String INSTANCE_BORDERWIDTH = "borderwidth";
    private static final String INSTANCE_MAX = "max";
    private static final String INSTANCE_FINISHED_STROKE_COLOR = "finished_stroke_color";
    private static final String INSTANCE_FINISHED_GRADIENT_STROKE_COLOR = "finished_gradient_stroke_color";
    private static final String INSTANCE_UNFINISHED_STROKE_COLOR = "unfinished_stroke_color";
    private static final String INSTANCE_ARC_ANGLE = "arc_angle";
    private static final String INSTANCE_SUFFIX = "suffix";

    public ArcProgress(Context context) {
        this(context, null);
    }

    public ArcProgress(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ArcProgress(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        default_text_size = Utils.sp2px(getResources(), 18);
        min_size = (int) Utils.dp2px(getResources(), 100);
        default_text_size = Utils.sp2px(getResources(), 40);
        default_suffix_text_size = Utils.sp2px(getResources(), 15);
        default_suffix_padding = Utils.dp2px(getResources(), 4);
//        default_suffix_text = "%";
        default_suffix_text = "";//FIXME how set default_suffix_text
        default_bottom_text_size = Utils.sp2px(getResources(), 10);
        circlePadding = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, getResources().getDisplayMetrics());//FIXME
        final TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.ArcProgress, defStyleAttr, 0);
        initByAttributes(a);
        a.recycle();
        initPainters();
    }

    protected void initByAttributes(TypedArray attributes) {
        int styleid = attributes.getResourceId(R.styleable.ArcProgress_master_finish_gradient_color, -1);
        if (styleid > -1) {
            mMasterFinishGradientColor = getResources().getIntArray(styleid);
            mKeepingGradientColor = getResources().getIntArray(styleid);
        }
        styleid = attributes.getResourceId(R.styleable.ArcProgress_slave_finish_gradient_color, -1);
        if (styleid > -1) {
            mSlaveFinishGradientColor = getResources().getIntArray(styleid);
        }
        mArcBackgroundColor = attributes.getColor(R.styleable.ArcProgress_arc_back_color, DEFAULT_ARC_BACKGROUND_COLOR);
        mStageChange = attributes.getFraction(R.styleable.ArcProgress_stage_change_threshold, 1, 1, DEFAULT_STAGE);
        mStrokeWidth = attributes.getDimension(R.styleable.ArcProgress_front_width, Utils.dp2px(getResources(), 4));
        textColor = attributes.getColor(R.styleable.ArcProgress_arc_text_color, default_text_color);
        textSize = attributes.getDimension(R.styleable.ArcProgress_arc_text_size, default_text_size);
        mArcAngle = attributes.getFloat(R.styleable.ArcProgress_arc_angle, DEFAULT_ARCANGLE);
        setMaxValues(attributes.getInt(R.styleable.ArcProgress_arc_max, default_max));
        setCurrentValues(attributes.getInt(R.styleable.ArcProgress_arc_progress, 0));
        strokeWidth = attributes.getDimension(R.styleable.ArcProgress_arc_stroke_width, mStrokeWidth);
        suffixTextSize = attributes.getDimension(R.styleable.ArcProgress_arc_suffix_text_size, default_suffix_text_size);
        suffixText = TextUtils.isEmpty(attributes.getString(R.styleable.ArcProgress_arc_suffix_text)) ? default_suffix_text : attributes.getString(R.styleable.ArcProgress_arc_suffix_text);
        suffixTextPadding = attributes.getDimension(R.styleable.ArcProgress_arc_suffix_text_padding, default_suffix_padding);
        bottomTextSize = attributes.getDimension(R.styleable.ArcProgress_arc_bottom_text_size, default_bottom_text_size);
        bottomText = attributes.getString(R.styleable.ArcProgress_arc_bottom_text);
        mRotateMatrix = new Matrix();
    }

    private void initPainters() {
        mTextPaint = new TextPaint();
        mTextPaint.setColor(textColor);
        mTextPaint.setTextSize(textSize);
        mTextPaint.setAntiAlias(true);
        mBackCirclePaint = new Paint();
        mBackCirclePaint.setColor(mArcBackgroundColor);
        mBackCirclePaint.setAntiAlias(true);
        mBackCirclePaint.setStrokeWidth(strokeWidth);
        mBackCirclePaint.setStyle(Paint.Style.STROKE);
        mBackCirclePaint.setStrokeCap(Paint.Cap.ROUND);
    }

    private void initGradient() {
        final float centerX = (getMeasuredWidth() - circlePadding) / 2;
        final float centery = (getMeasuredHeight() - circlePadding) / 2;
        final int count = mMasterFinishGradientColor.length;
        float[] positions = new float[count];
        for (int i = 0; i < count; ++i) {
            positions[i] = ((float) i * 1 / (count + 1)) + 0.01f;
        }
        mSweepGradient = new SweepGradient(centerX, centery, mMasterFinishGradientColor, positions);
    }

    @Override
    public void invalidate() {
        initPainters();
        super.invalidate();
    }

    public float getStrokeWidth() {
        return strokeWidth;
    }

    public void setStrokeWidth(float strokeWidth) {
        this.strokeWidth = strokeWidth;
        this.invalidate();
    }

    public float getSuffixTextSize() {
        return suffixTextSize;
    }

    public void setSuffixTextSize(float suffixTextSize) {
        this.suffixTextSize = suffixTextSize;
        this.invalidate();
    }

    public String getBottomText() {
        return bottomText;
    }

    public void setBottomText(String bottomText) {
        this.bottomText = bottomText;
        this.invalidate();
    }

    public int getCurrentValues() {
        return mProgress;
    }

    public void setCurrentValues(int progress) {
        if (((float) progress / max) <= mStageChange) {
            mMasterFinishGradientColor = mSlaveFinishGradientColor;
        } else {
            mMasterFinishGradientColor = mKeepingGradientColor;
        }
        this.mProgress = progress;
        if (this.mProgress > getMaxValues()) {
            this.mProgress %= getMaxValues();
        }
        invalidate();
    }

    public int getMaxValues() {
        return max;
    }

    public void setMaxValues(int max) {
        if (max > 0) {
            this.max = max;
            invalidate();
        }
    }

    public float getBottomTextSize() {
        return bottomTextSize;
    }

    public void setBottomTextSize(float bottomTextSize) {
        this.bottomTextSize = bottomTextSize;
        this.invalidate();
    }

    public float getTextSize() {
        return textSize;
    }

    public void setTextSize(float textSize) {
        this.textSize = textSize;
        this.invalidate();
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
        this.invalidate();
    }

    public int[] getFinishedGradientStrokeColor() {
        return mMasterFinishGradientColor;
    }

    public void setFinishedGradientStrokeColor(int[] finishedGradientStrokeColorArray) {
        this.mMasterFinishGradientColor = finishedGradientStrokeColorArray;
        this.invalidate();
    }

    public int getArcBackgroundColor() {
        return mArcBackgroundColor;
    }

    public void setArcBackgroundColor(int arcBackgroundColor) {
        this.mArcBackgroundColor = arcBackgroundColor;
        this.invalidate();
    }

    public float getArcAngle() {
        return mArcAngle;
    }

    public void setArcAngle(float arcAngle) {
        this.mArcAngle = arcAngle;
        this.invalidate();
    }

    public String getSuffixText() {
        return suffixText;
    }

    public void setSuffixText(String suffixText) {
        this.suffixText = suffixText;
        this.invalidate();
    }

    public float getSuffixTextPadding() {
        return suffixTextPadding;
    }

    public void setSuffixTextPadding(float suffixTextPadding) {
        this.suffixTextPadding = suffixTextPadding;
        this.invalidate();
    }

    @Override
    protected int getSuggestedMinimumHeight() {
        return min_size;
    }

    @Override
    protected int getSuggestedMinimumWidth() {
        return min_size;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        int measureWidth = MeasureSpec.getSize(widthMeasureSpec);
//        int measureHeight = MeasureSpec.getSize(heightMeasureSpec);
//        setMeasuredDimension(Math.min(measureWidth, measureHeight), Math.min(measureWidth, measureHeight));
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
        final int width = MeasureSpec.getSize(widthMeasureSpec);
        mRectF.set(strokeWidth / 2f, strokeWidth / 2f, width - strokeWidth / 2f, MeasureSpec.getSize(heightMeasureSpec) - strokeWidth / 2f);
        final float radius = width / 2f;
        final float angle = (360 - mArcAngle) / 2f;
        arcBottomHeight = radius * (float) (1 - Math.cos(angle / 180 * Math.PI));
        initGradient();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float startAngle = mArcAngle / 2f;
        float finishedSweepAngle = mProgress / (float) getMaxValues() * mArcAngle;
        float finishedStartAngle = startAngle;
        if (mProgress == 0) finishedStartAngle = 0.01f;
        mBackCirclePaint.setColor(mArcBackgroundColor);
        canvas.drawArc(mRectF, startAngle, mArcAngle, false, mBackCirclePaint);
//        LinearGradient linearGradient = new LinearGradient(circlePadding, circlePadding,
//                getMeasuredWidth() - circlePadding,
//                getMeasuredHeight() - circlePadding,
//                mMasterFinishGradientColor, null, Shader.TileMode.MIRROR);
//        mBackCirclePaint.setShader(linearGradient);
//        canvas.drawArc(mRectF, finishedStartAngle, finishedSweepAngle, false, mBackCirclePaint);
        final float centerX = (getMeasuredWidth() - circlePadding) / 2;
        final float centery = (getMeasuredHeight() - circlePadding) / 2;
//        final int count = mMasterFinishGradientColor.length;
//        float[] positions = new float[count];
//        for (int i = 0; i < count; ++i) {
//            positions[i] = ((float) i * 1 / (count + 1)) + 0.15f;
//        }
//        SweepGradient mSweepGradient = new SweepGradient(centerX, centery, mMasterFinishGradientColor, positions);
        initGradient();
        mRotateMatrix.setRotate(startAngle - 15, centerX, centery);//FIXME  -15
        mSweepGradient.setLocalMatrix(mRotateMatrix);
        mBackCirclePaint.setShader(mSweepGradient);
        canvas.drawArc(mRectF, finishedStartAngle, finishedSweepAngle, false, mBackCirclePaint);


//        String text = String.valueOf(getCurrentValues());
//        if (!TextUtils.isEmpty(text)) {
//            mTextPaint.setColor(textColor);
//            mTextPaint.setTextSize(textSize);
//            float textHeight = mTextPaint.descent() + mTextPaint.ascent();
//            float textBaseline = (getHeight() - textHeight) / 2.0f;
//            canvas.drawText(text, (getWidth() - mTextPaint.measureText(text)) / 2.0f, textBaseline, mTextPaint);
//            mTextPaint.setTextSize(suffixTextSize);
//            float suffixHeight = mTextPaint.descent() + mTextPaint.ascent();
//            canvas.drawText(suffixText, getWidth() / 2.0f  + mTextPaint.measureText(text) + suffixTextPadding, textBaseline + textHeight - suffixHeight, mTextPaint);
//        }

        if (arcBottomHeight == 0) {
            float radius = getWidth() / 2f;
            float angle = (360 - mArcAngle) / 2f;
            arcBottomHeight = radius * (float) (1 - Math.cos(angle / 180 * Math.PI));
        }

        if (!TextUtils.isEmpty(getBottomText())) {
            mTextPaint.setTextSize(bottomTextSize);
            float bottomTextBaseline = getHeight() - arcBottomHeight - (mTextPaint.descent() + mTextPaint.ascent()) / 2;
            canvas.drawText(getBottomText(), (getWidth() - mTextPaint.measureText(getBottomText())) / 2.0f, bottomTextBaseline, mTextPaint);
        }
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        final Bundle bundle = new Bundle();
        bundle.putParcelable(INSTANCE_STATE, super.onSaveInstanceState());
        bundle.putFloat(INSTANCE_STROKE_WIDTH, getStrokeWidth());
        bundle.putFloat(INSTANCE_SUFFIX_TEXT_SIZE, getSuffixTextSize());
        bundle.putFloat(INSTANCE_SUFFIX_TEXT_PADDING, getSuffixTextPadding());
        bundle.putFloat(INSTANCE_BOTTOM_TEXT_SIZE, getBottomTextSize());
        bundle.putString(INSTANCE_BOTTOM_TEXT, getBottomText());
        bundle.putFloat(INSTANCE_TEXT_SIZE, getTextSize());
        bundle.putInt(INSTANCE_TEXT_COLOR, getTextColor());
        bundle.putInt(INSTANCE_PROGRESS, getCurrentValues());
        bundle.putInt(INSTANCE_MAX, getMaxValues());
//        bundle.putInt(INSTANCE_FINISHED_STROKE_COLOR, getFinishedStrokeColor());
        bundle.putIntArray(INSTANCE_FINISHED_GRADIENT_STROKE_COLOR, getFinishedGradientStrokeColor());
        bundle.putInt(INSTANCE_UNFINISHED_STROKE_COLOR, getArcBackgroundColor());
        bundle.putFloat(INSTANCE_ARC_ANGLE, getArcAngle());
        bundle.putString(INSTANCE_SUFFIX, getSuffixText());
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            final Bundle bundle = (Bundle) state;
            strokeWidth = bundle.getFloat(INSTANCE_STROKE_WIDTH);
            suffixTextSize = bundle.getFloat(INSTANCE_SUFFIX_TEXT_SIZE);
            suffixTextPadding = bundle.getFloat(INSTANCE_SUFFIX_TEXT_PADDING);
            bottomTextSize = bundle.getFloat(INSTANCE_BOTTOM_TEXT_SIZE);
            bottomText = bundle.getString(INSTANCE_BOTTOM_TEXT);
            textSize = bundle.getFloat(INSTANCE_TEXT_SIZE);
            textColor = bundle.getInt(INSTANCE_TEXT_COLOR);
            setMaxValues(bundle.getInt(INSTANCE_MAX));
            setCurrentValues(bundle.getInt(INSTANCE_PROGRESS));
//            finishedStrokeColor = bundle.getInt(INSTANCE_FINISHED_STROKE_COLOR);
            mMasterFinishGradientColor = bundle.getIntArray(INSTANCE_FINISHED_GRADIENT_STROKE_COLOR);
            mArcBackgroundColor = bundle.getInt(INSTANCE_UNFINISHED_STROKE_COLOR);
            suffixText = bundle.getString(INSTANCE_SUFFIX);
            initPainters();
            super.onRestoreInstanceState(bundle.getParcelable(INSTANCE_STATE));
            return;
        }
        super.onRestoreInstanceState(state);
    }
}
