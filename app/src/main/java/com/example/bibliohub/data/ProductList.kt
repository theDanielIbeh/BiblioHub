package com.example.bibliohub.data

import com.example.bibliohub.data.entities.product.Product

object ProductList {
    fun list(): List<Product> =
        arrayListOf(
            Product(
                id = 1,
                title = "The No-Show",
                author = "Beth O'Leary",
                description = "The funny, heart-breaking and uplifting new novel from the author of The Flatshare *The instant Sunday Times Top 5 bestseller* 'Ingenious, heartwarming and romantic' SOPHIE KINSELLA 'Surprising and deeply satisfying' EMILY HENRY 'The kind of book that leaves an impression on your heart' HOLLY MILLER 'Such a clever, finely woven, sweet and heart-rending story' BOLU BABALOLA Three women. Three dates. One missing man... 8.52 a.m. Siobhan is looking forward to her breakfast date with Joseph. She was surprised when he suggested it - she normally sees him late at night in her hotel room. Breakfast on Valentine's Day surely means something ... so where is he? 2.43 p.m. Miranda's hoping that a Valentine's Day lunch with Carter will be the perfect way to celebrate her new job. It's a fresh start and a sign that her life is falling into place: she's been dating Carter for five months now and things are getting serious. But why hasn't he shown up? 6.30 p.m. Joseph Carter agreed to be Jane's fake boyfriend at an engagement party. They've not known each other long but their friendship is fast becoming the brightest part of her new life in Winchester. Joseph promised to save Jane tonight. But he's not here... Meet Joseph Carter. That is, if you can find him. The No-Show is the brilliantly funny, heart-breaking and joyful new novel from Beth O'Leary about dating, and waiting, and the ways love can find us. An utterly extraordinary tearjerker of a book, this is O'Leary's most ambitious novel yet. 'The kind of book you want all your friends to read so that you can talk about it non-stop' LIZZIE DAMILOLA BLACKBURN 'A brilliant, multilayered, romantic stunner' LAUREN HO 'Sweepingly romantic, bursting with character, and so, so clever' GILLIAN McALLISTER 'A superb rom-com with a heart wrenching twist' DAILY EXPRESS 'Perfect escapism: romantic and uplifting with a twist you won't see coming' LUCY DIAMOND 'Charming and delightful!' LAURA WILLIAMS 'It will break your heart in a million different ways' LOUISE O'NEILL 'A truly brilliant book' LUCY VINE 'Fresh and surprising yet still so distinctly Beth O'Leary' CAROLINE HULSE 'This book will stop-start your heart with plenty of tears and plenty of laughter' LIA LOUIS 'Fresh, clever, superbly plotted . . . A triumph' MIKE GAYLE 'Beth O'Leary at her very best' LINDSEY KELK 'Achingly clever, another fabulous read from Beth O'Leary' SOPHIE COUSENS A Sunday Times Top 5 bestseller w/c 24/04/2022",
                isbn = "9781529409123",
                quantity = 10,
                imgSrc = "http://books.google.com/books/content?id=lVUXEAAAQBAJ&printsec=frontcover&img=1&zoom=1&edge=curl&source=gbs_api",
                pubDate = "05-04-2022",
                price = "10.5",
                category = "Fiction"
            ), Product(
                id = 2,
                title = "The Only Study Guide You'll Ever Need",
                author = "Jade Bowler",
                description = "We've all been there: a new school year starts and there's 8 months till your exams - that's plenty of time, right? Then there's 6 months, 3 months, 1 month and oh, now there's 2 weeks left and you haven't started studying... What happens next is a panic-induced mayhem of highlighting everything in the textbook (without even questioning if it's actually helpful). But I'm here to help you change this! In The Only Study Guide You'll Ever Need, I'll cover a range of different topics including: · How to get started and pick up that pen · Learning techniques that actually work (hello, science of memory!) · The dos and don'ts of timetabling · And combatting fear of failure, perfectionism, exam stress and so much more! As a fellow student now at university, I definitely don't have a PhD in Exam Etiquette but this is the book younger me needed. All I wanted was one place that had a variety of tried-and-tested methods with reassurance from someone who had recently been through the education system. The Only Study Guide You'll Ever Need is just that, and I have collected the best techniques and tools I wish I'd known earlier to help you get through your studies and smash your exams! Jade x",
                isbn = "9781788704205",
                quantity = 10,
                imgSrc = "http://books.google.com/books/content?id=PVstEAAAQBAJ&printsec=frontcover&img=1&zoom=1&edge=curl&source=gbs_api",
                pubDate = "05-08-2021",
                price = "10.5",
                category = "Non-fiction"
            ), Product(
                id = 3,
                title = "The Course of True Love (and First Dates)",
                author = "Cassandra Clare",
                description = "Magnus Bane and Alec Lightwood might fall in love—but first they have a first date. One of ten adventures in The Bane Chronicles. When Magnus Bane, warlock, meets Alec Lightwood, Shadowhunter, sparks fly. And what happens on their first date lights a flame... This standalone e-only short story illuminates the life of the enigmatic Magnus Bane, whose alluring personality populates the pages of the #1 New York Times bestselling series The Mortal Instruments and The Infernal Devices. This story in The Bane Chronicles, The Course of True Love (and First Dates), is written by Cassandra Clare.",
                isbn = "9781442495630",
                quantity = 10,
                imgSrc = "http://books.google.com/books/content?id=GdgeAAAAQBAJ&printsec=frontcover&img=1&zoom=1&edge=curl&source=gbs_api",
                pubDate = "18-03-2014",
                price = "12.5",
                category = "Fiction"
            ), Product(
                id = 4,
                title = "Knowledge is Beautiful",
                author = "David McCandless",
                description = "A fascinating and thoroughly modern glimpse of world knowledge. It offers a deeper, more ranging look at the world and its history, and an entirely democratic, global look at key issues bedded into the foundations of world knowledge - from questions and facts on history and politics to science, literature and more.",
                isbn = "9780007427925",
                quantity = 10,
                imgSrc = "http://books.google.com/books/content?id=ZpQ6YgEACAAJ&printsec=frontcover&img=1&zoom=1&source=gbs_api",
                pubDate = "25-09-2014",
                price = "12.5",
                category = "Non-fiction"
            ), Product(
                id = 5,
                title = "The Thirteenth Tale",
                author = "Diane Setterfield",
                description = "When her health begins failing, the mysterious author Vida Winter decides to let Margaret Lea, a biographer, write the truth about her life, but Margaret needs to verify the facts since Vida has a history of telling outlandish tales.",
                isbn = "9780743298032",
                quantity = 10,
                imgSrc = "http://books.google.com/books/content?id=IhyOIfJk0GcC&printsec=frontcover&img=1&zoom=1&edge=curl&source=gbs_api",
                pubDate = "09-10-2007",
                price = "12.5",
                category = "Fiction"
            )
        )
}