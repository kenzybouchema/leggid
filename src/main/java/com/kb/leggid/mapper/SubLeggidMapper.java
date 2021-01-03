package com.kb.leggid.mapper;
import com.kb.leggid.dto.SubLeggidDto;
import com.kb.leggid.model.Post;
import com.kb.leggid.model.SubLeggid;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

// Indique que c'est un mapper de MapStruct, et qu'il doit être vu par spring comme un composant spring
// --> Spring gère l'injéction etc...
@Mapper(componentModel = "spring")
public interface SubLeggidMapper {

    // Ici  le mamping n'est pas direct on a une liste d'une part et dans le dto un entier
    // MapStruct permet de définir une méthode à appeler pour implémenter la logique dans l'attribut "expression"
    // Le reste des champs sont implicitement reconnu
    @Mapping(target = "numberOfPosts", expression = "java(mapPosts(subreddit.getPosts()))")
    SubLeggidDto mapSubredditToDto(SubLeggid subreddit);

    default Integer mapPosts(List<Post> numberOfPosts) {
        return numberOfPosts.size();
    }

    // Indique que c'est le mapping inverse du mapping existant
    @InheritInverseConfiguration
    @Mapping(target = "posts", ignore = true)
    SubLeggid mapDtoToSubreddit(SubLeggidDto subreddit);
}
