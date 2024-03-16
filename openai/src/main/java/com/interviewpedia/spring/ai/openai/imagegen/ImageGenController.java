package com.interviewpedia.spring.ai.openai.imagegen;

import org.springframework.ai.image.ImageClient;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class ImageGenController {

    @Autowired
    private ImageClient imageClient;

    @PostMapping("/ai/imagegen")
    public String imageGen(@RequestBody ImageGenRequest imageGenRequest) {
        ImagePrompt imagePrompt = new ImagePrompt(imageGenRequest.prompt());
        ImageResponse response = imageClient.call(imagePrompt);

        String imageUrl = response.getResult().getOutput().getUrl();
        return "redirect:" + imageUrl;
    }
}

