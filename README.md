Original App Design Project - README Template
===

# SCHOOL Q&A

## Table of Contents
1. [Overview](#Overview)
1. [Product Spec](#Product-Spec)
1. [Wireframes](#Wireframes)
2. [Schema](#Schema)

## Overview

### Description
The app is mainly for students at the CSUEB. It serve as a small community for students to easily access to different resources to resolve their problems.

### App Evaluation
[Evaluation of your app across the following attributes]
- **Category:** University Mobile Application
- **Mobile:**   This is an Android app
- **Story:**    User can search for questions, save & keep track of their posted questions and other questions from other users. 
- **Market:**   The app is for students at the CSUEB
- **Habit:**    This app will give student an easy access to find the answers from other users from CSUEB.
- **Scope:**    User are students from the university

## Product Spec

### 1. User Stories (Required and Optional)

**Required Must-have Stories**
* User can login
* User can register account 
* User can search question
* User can post new question
* User can answer question
* User can have their own profile: User can see their own posts

**Optional Nice-to-have Stories**
* User can view how many answers did a user answered - Checking creditability of other users...
* User register account with their school email - Identify users
* User can modify their profile
* User can visit other user's profile
* User can upvote a post
* User delete/ Mark 'completed' post 
* When user post a question, check if question is already in the database and suggest to direct user to the current post instead
* Camera/Photo Library feature - add photo along with question
* Administrator Account - monitor posts (delete/add), 

### 2. Screen Archetypes

*    Login screen: user can login their account
*    Register screen: user can register for an account
*    Search screen: user can search for Q&A posts based on their searching question, phrase, terms
*    Compose question screen: user can compose a question post. (optional) User can upload image from photo library along with the question
*    Post's content screen (showing questions, answers, and comment box): user can view the question, list of answers, and a comment box to add their answer to question
*    User's Profile screen: user can view their basic profile, user's question posts and (option) user's favorite posts.  

### 3. Navigation

**Tab Navigation** (Tab to Screen)

* User tab  
* Search tab
* (optional) User's favorite posts tab
   
**Flow Navigation** (Screen to Screen)

* User tab
    * If not yet logged in -> login/register screen
    * If logged-in:
        * User's posts.
            *  (optional) user can delete their post.
            *  (optional) mark 'completed' if their issue is resolved
* Search tab
    * Search by full-sentence question
    * Search by phrase/terms
    * Search by category
    * Get a list of posts
        * (When click on particular post) -> Post's content screen
            * Get list of answers for that particular post
            * User can add text answer to Comment box. 
                * (optional) user can add image along with the text as their answer to the Comment box

* (optional) User's favorite posts tab
    *  Keep track of posts user are interested in
        *  Update user if someone add answers to 'favorite' posts
## Wireframes
[Add picture of your hand sketched wireframes in this section]
<img src="https://github.com/CodePath-Team-5/SCHOOL-Q-A/blob/main/wireframe_image.png" width=600>

### [BONUS] Digital Wireframes & Mockups

### [BONUS] Interactive Prototype

## Schema 
[This section will be completed in Unit 9]
### Models & Networking
<img src="https://github.com/CodePath-Team-5/SCHOOL-Q-A/blob/main/schoolQA.jpg
" width=600>
