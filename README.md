Original App Design Project - README Template
===

# SCHOOL Q&A

## Table of Contents
1. [Overview](#overview)
1. [Product Spec](#product-spec)
1. [Wireframes](#wireframes)
2. [Schema](#schema)
3. [Sprint 1](#sprint1)
4. [Sprint 2](#sprint2)
5. [Sprint 3](#sprint3)
6. [Sprint 4](#sprint4)

## Overview
 ### [ Youtube Live demo link 🎥 ](https://www.youtube.com/channel/UCaRbt9rsjsysCJI8TP2YtEg/videos)

<p float="left">
   Search Function
   <img src= "https://github.com/CodePath-Team-5/SCHOOL-Q-A/blob/main/sprint4_search.gif" width=200px>
   Post Activity                                                   
   <img src="https://github.com/CodePath-Team-5/SCHOOL-Q-A/blob/main/sprint4_post.gif" width=200px>
    Profile Activity 
   <img src="https://github.com/CodePath-Team-5/SCHOOL-Q-A/blob/main/sprint4_profile.gif" width=200px>
   Edit Profile Activity                                                   
   <img src="https://github.com/CodePath-Team-5/SCHOOL-Q-A/blob/main/sprint_edit_profile.gif" width=200px>
   Compose Activity 
   <img src="https://github.com/CodePath-Team-5/SCHOOL-Q-A/blob/main/sprint4_compose.gif" width=200px>
</p>

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
* [X] User can login
* [X] User can register account 
* [X] User can view most recent question posts in the Search Page
* [X] User can type in keyword/sentence to search for related posts.
* [X] User can post new question
* [X] User can comment to a question
* [X] User can have their own profile: User can see their own posts

**Optional Nice-to-have Stories**
* [X] Login: User must register account with their school email (csueb email)
* [X] Register: User must meet certain password criterias in order to regiester for an account.
* [X] Profile: User can view their personal info on Profile
* [X] Profile: User can modify their profile. User can change: user image, username, password, user's info
* [X] Profile: User can view list of their posts and comments in Profile
* [X] Profile: User can delete their posts. User can undo the task if they change their mind
* [X] Profile: User can delete their comments. User can undo the task if they change their mind
* [X] Post: User can view other user's profile by clicking on user's image under comment section
* [X] Post: User can upvote a post
* [X] Post: Capture Photo feature. User can send comment with attached image
* [X] Compose: Capture Photo feature. User can upload a post question with attached image.

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
<img src="https://github.com/CodePath-Team-5/SCHOOL-Q-A/blob/main/schoolQA.png" width=600>

## Sprint 1
- Design layouts for activities  - Hao Lam
- Set up Back4App                - Margaret Hu

###  Update project:
- Back4app had been set up  to save user & post data
- Layout for activities had been designed. 
- Project can run with simple buttons to move from 1 activity to another

<p float="left">
   Project simple run: 
   <img src="https://github.com/CodePath-Team-5/SCHOOL-Q-A/blob/main/sprint1_update(1).gif" width=200px >
   Project Activity Layouts: 
   <img src="https://github.com/CodePath-Team-5/SCHOOL-Q-A/blob/main/sprint1_update(2).gif" width=200px>
</p>

## Sprint 2
- Add Compose Button & Redesign Search Resutl Item Layout      - Hao Lam
- Setup Adapter for Comment List, Structure Compose Data Model - Hao Lam
- Setup Adapter for Search List, Structure User Data Model     - Yi-Nong Wei
- Setup Network - ParseApplication                             - Yi-Nong Wei
- Output: 
   <img src="https://github.com/CodePath-Team-5/SCHOOL-Q-A/blob/main/sprint2.gif" width=200px >
## Sprint 3
- Implement Login/Register feature               - Nischitha Chottanahalli Nagaraju
- Implement Search function                      - Yi-Nong Wei
- Implement Profile feature                      - Yi-Nong Wei
- Implement Compose Activity                     - Margaret Hu
- Test run:
<p float="left">
   Sign up feature - Fail version
   <img src= "https://github.com/CodePath-Team-5/SCHOOL-Q-A/blob/main/sprint3_signup_feature(1).gif" width=200px>
   Sign up feature - Sucess version                                                   
   <img src="https://github.com/CodePath-Team-5/SCHOOL-Q-A/blob/main/sprint3_signup_feature(2).gif" width=200px>
</p>
<p2 float="left">   
   Login, Profile, Compose feature: 
   <img src="https://github.com/CodePath-Team-5/SCHOOL-Q-A/blob/main/sprint3_login_profile_feature.gif" width=200px>
   Search feature:
   <img src="https://github.com/CodePath-Team-5/SCHOOL-Q-A/blob/main/sprint3_search_feature.gif" width=200px>
</p2>
                                                                                                 

## Sprint 4
- Implement Edit Profile                                                   -  Yi-Nong Wei
- Add refresh feature to SearchResult list, Comment list, Post list        -  Yi-Nong Wei
- Implement delete & undo delele feature on user posts and comments        -  Hao Lam
- Improve overall layout & user experience in Profile & Post Activity      -  Hao Lam
- Implement Capture Photo feature in Compose Activity                      -  Margaret Hu
- Test run:
<p float="left">
   Search Function
   <img src= "https://github.com/CodePath-Team-5/SCHOOL-Q-A/blob/main/sprint4_search.gif" width=200px>
   Post Activity                                                   
   <img src="https://github.com/CodePath-Team-5/SCHOOL-Q-A/blob/main/sprint4_post.gif" width=200px>
</p>
<p2 float="left">   
   Profile Activity 
   <img src="https://github.com/CodePath-Team-5/SCHOOL-Q-A/blob/main/sprint4_profile.gif" width=200px>
   Edit Profile Activity                                                   
   <img src="https://github.com/CodePath-Team-5/SCHOOL-Q-A/blob/main/sprint_edit_profile.gif" width=200px>
</p2>
<p3 float="left">   
   Compose Activity 
   <img src="https://github.com/CodePath-Team-5/SCHOOL-Q-A/blob/main/sprint4_compose.gif" width=200px>
</p3>
