---
layout: post
title:  "List of helper abstractions"
tags: dev-guide
order: 060
---

TODOC : GUI abstractions could be grouped in this page (since it need just few explanations) but non GUI abstractions needs dedicated pages since there is many use cases ... (see other TODOC in this page)

Those abstractions are the core of our system to developp apps, you will find stuff to deal with the beat sharing clock, saving presets, and specific abstractions to interact with a touchscreen or display a list of symbols.

They are all located in the */puredata* release folder, and each one has a help patch.

But first let's talk a bit how to workflow to interface pd with the code hierarchy of the eclipse project.

# Pure Data best practices

First you should prefer to work in a project folder, that is to say in the **assets** folder of a new project in folder named after you app name.

We advise you to create a pd **'launcher' patch** at the base of your project, to simulate the way it works on android when working on your desktop computer. In the AcidBass example project it is called *AcidBass_launcher_desktop.pd*, and it looks like this :

![pd launcher]({{site.baseurl}}/img/pd_desktop_launcher.png) TODOC : change path to "puredata" folder ?

This pd patch is only for use in desktop, and in developpement mode, it is not embeded in your application.

It contains a path to our project folder containing the abstractions listed below.

It also contains a path to the *assets/Acid_Bass* folder which contains each **gui-view** of the patch built as an abstraction containing only gui objects. And a **core abstraction** which contains all the audio, the logic, and the saving system implementation.

This setup will help you to build several gui view stacked in tabs for your app. You can check the 'how to configure your launcher post' to learn more about this view system.

# [clock]

The clock is the main abstraction of the beat sharing system. To sum up we have a custom midi clock built in java, it talks to libpd to resynch this internal clock to the master one (over wifi). So you will have to use this abstraction as your clock if you want to build apps that interface with our beat sharing system.

You will find a help patch associated with it. Just remember that midi formalism gives a 24 ticks to make a beat.

When developping with pd, you can use the [clock-gui] abstraction that will mimic the behavior of the application bar. It features play/pause/resume controls, master/slave mode, audio feedback, it talks to every instance of a clock abstraction via global messages.

[clock-gui] should be used in your **launcher** patch.
[clock] has to be used in your **core** abstraction.

TODOC : clock as itself might have a dedicated page to include compatibility : explain that we implements Raw IP Midi, explain that we implements standard midi clock specification, ... and so on.

# [loadsave]

Comes from DroidParty unchanged and is used combined with persist abstractions. It enables to route save and loading messages with some details. More about it in the 'How to configure you launcher' post in the section about 'persist'.

It takes as its only argument the name of the preset bank you want to save in. 
It should be used in your **core** abstraction. 

# [midiclockout]

This is a barebones midiclock out abstraction. You should be able to use it on a **desktop computer** to be the master of a bunch of tablets.

You will need something like QmidiNet (if you are on linux) to send the clock via wifi. Hopefully we will document this further in the future, and add alternatives for other operating systems. Get in touch if you can help !

TODOC this section should be part of "clock section"

# [persist abstractions]

Those abstractions should be used in your **core** abstraction. They are used for the state saving system.
More about it in the 'How to configure you launcher' post in the section about 'persist'.

TODOC link to persist tutorial (this tutorial might not be part of "how to configure" but a dedicated page since it's a big topic)

# [taplist]

TODOC as we talk about it, explain what it is and then tell it's derived from DroidPArty ... with or without changes

Comes from DroidParty but breaks compatibility with the original DroidParty abstraction. It emulates a list of symbol on which you can click/tap to go to the next element, on android it opens a popup dialog for you to select a value. 

It should be used in your **gui-view** abstractions.

The main reason for this break is the lack of label in the original implementation : we needed to one to enable overrides of specific element. So now the fifth argument is a label name.

There was another change for convenience, and to maintain symetry in the messages sent and received from the taplist abstraction. Third and fourth argument are send and receive names for the instance you create, those are waiting for *symbol* or *list* type of data but you can also access data via the index of the element of the list of argument.

For instance you create a taplist object with this name :
{% highlight  java%} 
[taplist 50 60 letter.s letter.r letter A B C D E F G H I J K L]
{% endhighlight %}

If you want to set the object to 'H' you can now send a symbol containing the character 'H' to letter.r, something like this :

{% highlight  java%} 
[bang]
|
[H (
|
[s letter.r]
{% endhighlight %}


you could only set the value via "index" before :

{% highlight  java%} 
[bang]
|
[f 8(
|
[s letter.r/idx]
{% endhighlight %} 



# [touch]

Comes from DroidParty unchanged. It emulated a 2D touch surface, that can be used as kaos-pad like interface.
It should be used in your **gui-view** abstractions.

TODOC as we talk about it, explain what it is and then tell it's derived from DroidPArty ...