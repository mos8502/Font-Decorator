package hu.anemeth.fontdecorator;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;


import java.util.Arrays;

/**
 * Created by nemi on 2015.06.28..
 */
public class FontDecoratingLayoutInflater extends LayoutInflater {
    private static final String[] sClassPrefixList = {
            "android.widget.",
            "android.webkit.",
            "android.view.",
            "android.app."
    };

    private static final int[] ATTRS;

    static {
        ATTRS = Arrays.copyOf(R.styleable.FontDecorator, R.styleable.FontDecorator.length + 1);
        ATTRS[ATTRS.length-1] = android.R.attr.textStyle;
    }


    @SuppressLint("NewApi")
    private static class FactoryMerger implements Factory2 {
        private final Factory f1, f2;
        private final Factory2 f12, f22;

        FactoryMerger(Factory f1, Factory2 f12, Factory f2, Factory2 f22) {
            this.f1 = f1;
            this.f2 = f2;
            this.f12 = f12;
            this.f22 = f22;
        }

        public View onCreateView(String name, Context context, AttributeSet attrs) {
            View v = f1.onCreateView(name, context, attrs);
            if (v != null) return v;
            return f2.onCreateView(name, context, attrs);
        }

        public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
            View v = f12 != null ? f12.onCreateView(parent, name, context, attrs)
                    : f1.onCreateView(name, context, attrs);
            if (v != null) return v;
            return f22 != null ? f22.onCreateView(parent, name, context, attrs)
                    : f2.onCreateView(name, context, attrs);
        }
    }

    class FactoryWrapper  implements LayoutInflater.Factory {
        LayoutInflater.Factory factory;

        public void setFactory(Factory factory) {
            this.factory = factory;
        }

        @Override
        public View onCreateView(String name, Context context, AttributeSet attrs) {
            View view;
            if(factory != null) {
                view = factory.onCreateView(name, context, attrs);
            } else {
                view = null;
            }

            if(view == null) {
                try {
                    view = createView(name, attrs);
                } catch (ClassNotFoundException ignore) {

                }
            }

            return decorate(view, context, attrs);
        }
    }

    @SuppressLint("NewApi")
    class FactoryWrapper2 extends FactoryWrapper implements Factory2 {
        LayoutInflater.Factory2 factory2;

        void setFactory(Factory2 factory) {
            this.factory = this.factory2 = factory;
        }

        @Override
        public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
            View view;
            if (factory2 != null) {
                view = factory2.onCreateView(parent, name, context, attrs);
            } else if(factory != null){
                view = factory.onCreateView(name, context, attrs);
            }  else {
                view = null;
            }

            if(privateFactory != null && view == null) {
                view = privateFactory.onCreateView(parent, name, context, attrs);
            }

            if(view == null) {
                try {
                    view = createView(name, attrs);
                } catch (ClassNotFoundException ignore) {

                }
            }

            return decorate(view, context, attrs);
        }

        @Override
        public View onCreateView(String name, Context context, AttributeSet attrs) {
            View view;

            if(factory != null) {
                view = factory.onCreateView(name, context, attrs);
            } else {
                view = null;
            }

            if(privateFactory != null && view == null) {
                view = privateFactory.onCreateView(name, context, attrs);
            }

            if(view == null) {
                try {
                    view = createView(name, attrs);
                } catch (ClassNotFoundException ignore) {

                }
            }

            return decorate(view, context, attrs);
        }
    }

    private final TypefaceProvider typefaceProvider;
    private FactoryWrapper2 factoryWrapper2;
    private FactoryWrapper factoryWrapper;
    private Factory2 privateFactory;

    public FontDecoratingLayoutInflater(Context context, TypefaceProvider typefaceProvider) {
        super(context);
        this.typefaceProvider = typefaceProvider;

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            FactoryWrapper2 factoryWrapper = new FactoryWrapper2();
            this.factoryWrapper = this.factoryWrapper2 = factoryWrapper;
        } else {
            FactoryWrapper factoryWrapper = new FactoryWrapper();
            this.factoryWrapper = factoryWrapper;
        }
    }

    public void setPrivateFactory(Factory2 factory) {
        if (privateFactory == null) {
            privateFactory = factory;
        } else {
            privateFactory = new FactoryMerger(factory, factory, privateFactory, privateFactory);
        }
    }

    private View createView(String name, AttributeSet attrs) throws ClassNotFoundException {
        View view = null;
        if(name.indexOf('.') == -1) {
            for (int i = 0; i < sClassPrefixList.length && view == null; i++) {
                try {
                    view = createView(name, sClassPrefixList[i], attrs);
                } catch (ClassNotFoundException e) {

                }
            }

            if(view == null) {
                throw new ClassNotFoundException(name);
            }
        } else {
            view = createView(name, null, attrs);
        }



        return view;
    }

    @Override
    protected View onCreateView(String name, AttributeSet attrs) throws ClassNotFoundException {
        View view = createView(name, attrs);
        return decorate(view, getContext(), attrs);
    }

    @Override
    public void setFactory(Factory factory) {
        factoryWrapper.setFactory(factory);
        super.setFactory(factoryWrapper);
    }

    @Override
    public void setFactory2(Factory2 factory2) {
        factoryWrapper2.setFactory(factory2);
        super.setFactory2(factoryWrapper2);
    }

    public LayoutInflater cloneInContext(Context newContext) {
        return new FontDecoratingLayoutInflater(newContext, typefaceProvider);
    }

    private View decorate(View view, Context context, AttributeSet attrs) {
        if (view instanceof TextView) {
            TextView textView = TextView.class.cast(view);

            TypedArray a = context.getTheme().obtainStyledAttributes(attrs, ATTRS, R.attr.fontDecoratorStyle, 0);
            String font = a.getString(R.styleable.FontDecorator_font);
            int style = a.getInt(ATTRS.length - 1, Typeface.NORMAL);
            a.recycle();

            Typeface typeface = typefaceProvider.get(font, style);
            if(typeface != null) {
                textView.setTypeface(typeface, style);
            }
        }

        return view;
    }
}
