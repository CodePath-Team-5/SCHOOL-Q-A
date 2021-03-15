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
User can:
    - post question
    - search questions & answer
    - answer question

### App Evaluation
[Evaluation of your app across the following attributes]
- **Category:**
- **Mobile:**
- **Story:**
- **Market:**
- **Habit:**
- **Scope:**

## Product Spec

### 1. User Stories (Required and Optional)

**Required Must-have Stories**
* User can login/ register account
* User can search question
* User can add new question
* User can answer question

**Optional Nice-to-have Stories**

* User delete/ Mark 'completed' post 
* When user post a question, check if question is already in the database and suggest to direct user to the current post instead
* Camera/Photo Libary feature - add photo along with question
* Administrator Account - monitor posts (delete/add), 

### 2. Screen Archetypes

*    Login screen: user can login their account
*    Register screen: user can register for an account
*    Search screen: user can search for Q&A posts based on their searching question,phrase,terms
*    Compose question screen: user can compose a question post. (optional) User can upload imgage from photo library along with the question
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
            *  (optional) mark 'completed' if their issue is ressolved
        * 
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
<img src="YOUR_WIREFRAME_IMAGE_URL" width=600>

### [BONUS] Digital Wireframes & Mockups

### [BONUS] Interactive Prototype

## Schema 
[This section will be completed in Unit 9]
### Models
[Add table of models]
### Networking
- [Add list of network requests by screen ]
- [Create basic snippets for each Parse network request]
- [OPTIONAL: List endpoints if using existing API such as Yelp]
