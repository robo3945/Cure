# Cure # 
CURE - Towards Enforcing a Reliable Timeline for Cloud Forensics: model, architecture, and experiments

Roberto Battistoni(a), Roberto Di Pietro(b), Flavio Lombardi(c)

(a) Sapienza University of Rome, Dipartimento di Informatica - via Salaria , 113 - Roma, Italy
(b) Cybersecurity Research Dept. Bell Labs - Paris, France
(c) IAC-CNR via dei Taurini, 19 00185 - Roma, Italy

## Abstract ##

A malicious alteration of system-provided timeline can negatively affect the reliability of computer forensics. Indeed, detecting such changes and possibly reconstructing the correct timeline of events is of paramount importance for court admissibility and logical coherence of collected evidence. However, reconstructing the correct timeline for a set of network nodes can be difficult since an adversary has a wealth of opportunities to disrupt the timeline and to generate a fake one. This aspect is exacerbated in cloud computing, where host and guest machine-time can be manipulated in various ways by an adversary. Therefore, it is important to guarantee the integrity of the timeline of events for cloud host and guest nodes, or at least to ensure that timeline alterations do not go undetected. This paper provides several contributions. First, we survey the issues related to cloud machine-time reliability. Then, we introduce a novel architecture (CURE) aimed at providing timeline resilience to cloud nodes. Further, we implement the proposed framework and extensively test it on both a simulated environment and on a real cloud. We evaluate and discuss collected results showing the effectiveness of our proposal.

Keywords: cloud computing, digital forensics, timeline validation, modeling, measurement and simulation, experimental test-beds and research platforms.

## HowTo ## 

Local Simulation args for the *Java Cure.jar*: 
"-lsim -nTc 10 -sp 50000 -kp ./cure.keystore -tf ./config/topology.prop.xml -tIp 127.0.0.1 -d 60 -td ../traces/ -cfg ./config/config.prop.xml"

Distributed Simulation args for the *Java Parser.jar*:
"../traces/"
