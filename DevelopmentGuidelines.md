# Development Guidelines for the SimPatrol Project #

This page describes the development process used in this project.



---

## 1. Introduction ##

This process is intended to help the coordination of the team by giving visibility about what each developer is doing and where the project is going. At the same time, the process is intedend to be simple, with little overhead.

Three general guidelines for the project are:

  1. The team will interact using the group: http://groups.google.com/group/simpatrol .
  1. All important changes to the project should be related to an "issue" registered in the project page.
  1. After major changes (or after several minor changes) the code should receive a milestone "tag".

More details are given below.



---

## 2. Code Components ##

Currently, the project has three **components** placed in different directories under the _trunk_:

  1. **_Core_** - it is the simulator server itself. It is in directory "_trunk\SimPatrol_".
  1. **_Clients_** - currently, this component includes client classes for agents, logs and metrics. It is in directory "_trunk\SimPatrol_".
  1. **_EnvEditor_** - it is the environment (graph) editor. It is in directory "_trunk\SimPatrol Environment Editor_".

All the components receive an only version number registered by a svn _tag_ (see "Tags and Versions").



---

## 3. Issues ##

The issue tracking system will be used no just to register bugs but also for "normal" development tasks.

Any code change plan should be initially discussed with the team, specially if it is a new feature. Then, it is necessary to register an _issue_ for the code change.

Before starting (developing) an issue, a member of the team should assure that the issue has at least four labels indicating:
  * the _type_ of the issue,
  * the _component_ to which the issue is related,
  * the _priority_ of the issue and
  * the _version_ in which the change will be released (i.e. the next version of the project).

Possible **types** of issues are:
  1. **_Defect_** - a bug or an undesirable feature.
  1. **_Enhancement_** - development of new features, new components, performance improvements, etc.
  1. **_Other_** - in other cases.

Possible **components** of the issues are those listed in the previous section. There are also options for changes in documentation and for creating a new component.

Possible **priorities** of the issues are: _critical_, _high_, _medium_ and _low_.

The developer chosen to solve an issue should take over its ownership and change its status to STARTED. Only then should he start coding. When the development is finished, he should change its status to DONE.



---

## 4. Versioning and Tags ##

The project as a whole (with all its components) is versioned according to the following pattern:

```
`<MAJOR>.<MINOR>`
```

The "major" part is incremented after changes that severely impact users (client designers). The "minor" part is used for less import changes. See the versions' history.

At any time after significative changes, the team may decide to increment its version number. In his case, the team must
  1. Test all the components and check all the components are working as expected.
  1. Assign a tag (label) to the repository, indicatin the version of the project.

The tag should follow the pattern:

```
`SIMPATROL_<VERSION>`
```

In a tag, the version will have dots (.) replaced by underscores (`_`).

Example tag: SIMPATROL\_2\_7



---

## 5. Other Remarks ##

Most issues should be developed in the main line. Only in special cases a branch may be created to develop an issue.

For Java code, developers should follow the Sun's [Code Conventions](http://java.sun.com/docs/codeconv/).