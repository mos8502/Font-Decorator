Font Decorator is a concept I've been working on to solve a problem:

Set font on views inflated from android layout XMLs.

One of the most straightforward solution is to subclass framework classes, declare new attributes and have a way of loading fonts.

Once this is done, go through your layouts and replace tags with full class path of your custom view implementations. Of course you'll have to do this for all classes that derive from TextView which includes EditText, RadioButton, CheckBox etc.

You can take things a bit further. You can hook in your LayoutInflaterFactory(2). This will save you from actually changing the tags in your layouts: you can instantiate for example your FontTextView instead of the framework provided TextView. Factories are used by the AppCompat implementation to inflate replacement views to back port some of the functionalities introduced in later version of the platform. They are also used to inflate fragments declared in your layout XMLs both in support and non support version of Activities.

There is one big problem with the way LayoutInflater has been implemented. You can only set a factory once, subsequent calls will throw an exception. So for example once AppCompat (or some other component) installs a factory you can't hook in you own. Also if you are not strictly sticking with AppCompat you have to have a parallel implementation of subclasses both from the framework and support libraries. This may result in lots of redundant code based on the implementation approach you are taking.

This proof of concept implementations works by deriving from LayoutInflater. It implements it's own factory wrappers that wrap the factory set on it. It is used to "intercept" all view "instantiations" and allow applying fonts externally. It works with both support and non support version of activities although the solution has not been thoroughly tested yet.

The approach I took has it's cavities though and they come from the way LayoutInflater is implemented and how it's being used by the framework.

LayoutInflater is an abstract class. The way it inflates tags like LinearLayout, RelativeLayout and others where you don't supply a fully qualified name is by applying a prefix to the name of the tag. LayoutInflater only tries to apply one such prefix "android.view.". But if you look at the View, ViewGroup derived classes in the SDK documentation you can clearly see that there are classes that are not int the android.view package... on the contrary, most of the classes are in android.widget and there are others scattered around :).

As a result you can't simply derive from LayoutInflater implementing it's one abstract methods (and constructors) as you'll loose the ability to inflate even the simplest layouts. If you look at the android source code you'll find a class called PhoneLayoutInflater. PhoneLayoutInflater extends LayoutInflater and introduces additional prefixes/packages to search for classes in. My implementation tries to apply the same set of prefixes but there is a problem:

Device manufacturers can customize the platform and they may introduce their own LayoutInflater implementation thay may apply different prefixes. Let's say one device manufaturer is not pleased by the way LinearLayout looks, so the decided to provide their own implementation "manufacturer.LinearLayout" and let their LayoutInflater implementation return this instead of the framework provided one when inflating from layout resource. Taking my approach you would not be able to inflate this as it does not know about the additional package to look for classes in. On the other hand this is less likely as they would introduce an inconsistency anyway as you can instantiate LinearLayout from code as well.

A more likely source of error is if a newer version of android introduced a new View, ViewGroup that can't be found in the set of prefixes. 
